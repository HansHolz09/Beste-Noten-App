"""TEMPORARY: diagnose the Windows ARM64 Gradle shutdown timeout; remove after diagnosis."""

import os
from pathlib import Path
import subprocess
import sys
import threading
import time


def main():
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    command = sys.argv[1:]
    if command[:1] == ["--"]:
        command.pop(0)
    if not command:
        raise SystemExit("Expected the Gradle command after --")
    if os.name == "nt" and command[0] == "./gradlew":
        command[0] = "gradlew.bat"

    output = Path(os.environ["RUNNER_TEMP"]) / "windows-arm64-debug"
    output.mkdir(parents=True, exist_ok=True)
    java_home = Path(os.environ["JAVA_HOME"])
    suffix = ".exe" if os.name == "nt" else ""
    jcmd = java_home / "bin" / f"jcmd{suffix}"
    stop = threading.Event()
    packaging_finished = threading.Event()
    last_output = time.monotonic()

    # Artifacts do not get GitHub's console secret masking. Redact known credentials here too.
    secrets = [
        value for key, value in os.environ.items()
        if len(value) >= 8 and any(word in key.upper() for word in ("TOKEN", "SECRET", "PASSWORD", "CLIENT_ID"))
    ]

    def redact(text):
        for secret in secrets:
            text = text.replace(secret, "***")
        return text

    def diagnostic(args, destination):
        try:
            result = subprocess.run(args, capture_output=True, encoding="utf-8", errors="replace", timeout=8)
            text = f"exit={result.returncode}\n{result.stdout}\n{result.stderr}"
        except (OSError, subprocess.TimeoutExpired) as error:
            text = f"Diagnostic command failed: {type(error).__name__}: {error}\n"
        destination.write_text(redact(text), encoding="utf-8")
        return text

    def watch():
        sequence = 0
        while not stop.wait(15):
            # Capture quiet phases as well as the critical 60 seconds after electron-builder exits.
            if not packaging_finished.is_set() and time.monotonic() - last_output < 30:
                continue
            sequence += 1
            stamp = f"{sequence:03d}-{time.strftime('%H%M%S', time.gmtime())}Z"
            print(f"[TEMP Windows ARM64 debug] Capturing JVM threads ({stamp})", flush=True)
            listing = diagnostic([str(jcmd), "-l"], output / f"{stamp}-jvms.txt")
            for line in listing.splitlines():
                if stop.is_set():
                    break
                fields = line.split()
                if fields and fields[0].isdigit() and "org.gradle.launcher.daemon.bootstrap.GradleDaemon" in line:
                    diagnostic(
                        [str(jcmd), fields[0], "Thread.print", "-l"],
                        output / f"{stamp}-gradle-{fields[0]}-threads.txt",
                    )

    diagnostic([str(java_home / "bin" / f"java{suffix}"), "-version"], output / "java-version.txt")
    monitor = threading.Thread(target=watch, daemon=True)
    monitor.start()
    try:
        # Windows .bat launchers need cmd.exe. The task and flags are supplied by the workflow.
        with subprocess.Popen(
            command, shell=os.name == "nt", stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
            encoding="utf-8", errors="replace", bufsize=1,
        ) as process, (output / "gradle-build.log").open("w", encoding="utf-8") as log:
            for line in process.stdout:
                last_output = time.monotonic()
                if "nucleus builder package written to" in line:
                    packaging_finished.set()
                safe_line = redact(line)
                log.write(safe_line)
                log.flush()
                print(safe_line, end="", flush=True)
            return_code = process.wait()
        (output / "exit-code.txt").write_text(str(return_code), encoding="utf-8")
        return return_code
    finally:
        stop.set()
        monitor.join(timeout=20)
        report = Path("build/reports/problems/problems-report.html")
        if report.is_file():
            try:
                (output / report.name).write_text(
                    redact(report.read_text(encoding="utf-8", errors="replace")), encoding="utf-8",
                )
            except OSError as error:
                print(f"[TEMP Windows ARM64 debug] Could not save problems report: {error}", flush=True)


if __name__ == "__main__":
    sys.exit(main())
