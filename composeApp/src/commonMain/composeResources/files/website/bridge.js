(function () {
  if (window.location.origin !== '{{origin}}' || window.besteSchuleApp) return;
  var seq = 0;
  var pending = {};
  function post(type, arg) {
    return new Promise(function (resolve) {
      var id = String(++seq);
      pending[id] = resolve;
      try { {{postMessage}} } catch (e) { delete pending[id]; resolve(null); }
    });
  }
  function unsupported() { return Promise.resolve({ status: 'error', code: 'unsupported' }); }
  function token(type) {
    return post(type, '').then(function (token) { return typeof token === 'string' && token ? token : null; });
  }
  Object.defineProperty(window, '__bnaResolve', {
    value: function (id, result) {
      var resolve = pending[id];
      if (!resolve) return;
      delete pending[id];
      resolve(result);
    },
  });
  window.besteSchuleApp = Object.freeze({
    isApp: true,
    bridgeVersion: 2,
    platform: '{{platform}}',
    auth: {
      getToken: function () { return token('getToken'); },
      login: unsupported,
      reauthenticate: function () {
        return token('reauthenticate').then(function (token) { return token ? { status: 'ok' } : { status: 'login_required' }; });
      },
      logout: function () { return post('close', '').then(function () { return { status: 'ok' }; }); },
      getSignInStats: unsupported,
    },
    scanQR: function () { return Promise.resolve({ status: 'error', code: 'unavailable' }); },
    notifications: {
      getPermission: unsupported,
      requestPermission: unsupported,
      getDeviceToken: function () { return Promise.resolve(null); },
      getConsent: unsupported,
      setConsent: unsupported,
      clear: unsupported,
      clearAll: unsupported,
      setBadge: unsupported,
    },
    updates: { getState: unsupported, check: unsupported, applyPending: unsupported },
    review: { isAvailable: unsupported, request: unsupported },
    promptStoreUpdate: unsupported,
    openExternal: function (url) { return post('openExternal', String(url)).then(function () { return { status: 'ok' }; }); },
  });

  function safe(name, block) {
    return function () {
      try { return block.apply(null, arguments); } catch (e) { post('problem', name + ': ' + (e && e.message)); }
    };
  }
  safe('week', function () {
    var NativeResizeObserver = window.ResizeObserver;
    if (!NativeResizeObserver) return;
    function widen(entry) {
      var root = getComputedStyle(document.documentElement);
      if (entry.target.tagName !== 'MAIN' || location.pathname.indexOf('/journal') < 0 || root.getPropertyValue('--bna-tweaks').trim() !== '1') return entry;
      var rect = entry.contentRect;
      var weekWidth = 50.875 * (parseFloat(root.fontSize) || 16) + 30;
      var width = Math.max(rect.width, weekWidth);
      return {
        target: entry.target,
        contentRect: { x: rect.x, y: rect.y, top: rect.top, left: rect.left, bottom: rect.bottom, right: rect.left + width, width: width, height: rect.height },
        borderBoxSize: entry.borderBoxSize,
        contentBoxSize: entry.contentBoxSize,
        devicePixelContentBoxSize: entry.devicePixelContentBoxSize,
      };
    }
    window.ResizeObserver = function (callback) {
      return new NativeResizeObserver(function (entries, observer) {
        callback(entries.map(widen), observer);
        if (entries.some(function (entry) { return entry.target.tagName === 'MAIN'; })) restoreMobile();
      });
    };
    window.ResizeObserver.prototype = NativeResizeObserver.prototype;
  })();
  function tweaksEnabled() {
    return getComputedStyle(document.documentElement).getPropertyValue('--bna-tweaks').trim() === '1';
  }
  function withDayView(query, width) {
    return width < 830 && !query.display;
  }
  var startJournalWithDay = safe('weekStart', function () {
    if (location.pathname.indexOf('/journal') < 0) return;
    var url = new URL(location.href);
    if (!withDayView(Object.fromEntries(url.searchParams), innerWidth) || !tweaksEnabled()) return;
    url.searchParams.set('display', 'day');
    history.replaceState(history.state, '', url);
  });
  var mobileOverride = null;
  function environmentStore() {
    var app = document.getElementById('app');
    var pinia = app && app.__vue_app__ && app.__vue_app__.config.globalProperties.$pinia;
    return pinia && pinia._s && pinia._s.get('environment');
  }
  function suspendMobile() {
    var store = environmentStore();
    if (!store || mobileOverride !== null || !store._isMobile) return;
    mobileOverride = store._isMobile;
    store._isMobile = false;
  }
  function restoreMobile() {
    if (mobileOverride === null) return;
    var store = environmentStore();
    if (store) store._isMobile = mobileOverride;
    mobileOverride = null;
  }
  var routerGuarded = false;
  var guardRouter = safe('router', function () {
    var app = document.getElementById('app');
    var router = !routerGuarded && app && app.__vue_app__ && app.__vue_app__.config.globalProperties.$router;
    if (!router) return;
    routerGuarded = true;
    router.beforeEach(function (to) {
      var main = document.querySelector('main');
      if (to.path.indexOf('/journal') < 0 || !tweaksEnabled()) return true;
      if (to.query.weekOff) return false;
      if (to.query.display === 'week') suspendMobile();
      if (!withDayView(to.query, main ? main.clientWidth : innerWidth)) return true;
      return { path: to.path, hash: to.hash, query: Object.assign({}, to.query, { display: 'day' }) };
    });
  });
  function define(name, value) {
    Object.defineProperty(window, name, { value: safe(name, value) });
  }
  function styleElement(id) {
    var style = document.createElement('style');
    style.id = id;
    return style;
  }
  function nextFrames() {
    return new Promise(function (resolve) { requestAnimationFrame(function () { requestAnimationFrame(resolve); }); });
  }
  var fontStyle = null;
  var themeStyle = null;
  var backdropElement = null;
  var backdropUrl = null;
  var backdropShown = Promise.resolve();
  function keepLast() {
    var head = document.head || document.documentElement;
    [fontStyle, themeStyle].forEach(function (style) { if (style) head.appendChild(style); });
  }
  var adaptInsets = safe('insets', function () {
    Array.prototype.forEach.call(document.styleSheets, function (sheet) {
      var rules;
      try { rules = sheet.cssRules; } catch (e) { return; }
      (function walk(list) {
        Array.prototype.forEach.call(list, function (rule) {
          if (rule.cssRules) walk(rule.cssRules);
          if (rule.style && rule.style.cssText.indexOf('safe-area-inset-bottom') >= 0) {
            rule.style.cssText = rule.style.cssText.replace(/env\(\s*safe-area-inset-bottom\s*(,[^)]*)?\)/g, 'var(--bna-inset-bottom, 0px)');
          }
        });
      })(rules || []);
    });
  });
  var menuUsable = {};
  var probing = false;
  function hasUsableContent(element) {
    var text = (element.textContent || '').replace(/\s/g, '');
    element.querySelectorAll('[data-bna-hidden]').forEach(function (hidden) {
      text = text.replace((hidden.textContent || '').replace(/\s/g, ''), '');
    });
    return text.length > 0;
  }
  function probeMenu(menu, key) {
    var toggle = menu.querySelector('button');
    if (!toggle) return;
    probing = true;
    document.documentElement.setAttribute('data-bna-probing', '');
    toggle.click();
    nextFrames().then(function () {
      var body = document.querySelector('.v-sheet-body');
      menuUsable[key] = true;
      if (body) {
        markUnusable(body);
        menuUsable[key] = hasUsableContent(body);
        document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', bubbles: true }));
      }
      return new Promise(function (resolve) { setTimeout(resolve, 400); });
    }).then(function () {
      document.documentElement.removeAttribute('data-bna-probing');
      probing = false;
      scheduleUpdate();
    });
  }
  function markUnusable(root) {
    root.querySelectorAll('.dropdown-item, .btn').forEach(function (item) {
      var text = (item.textContent || '').trim();
      if (text.length < 40 && /drucken|print|export/i.test(text)) item.setAttribute('data-bna-hidden', '');
    });
    if (!{{fileUploads}}) {
      root.querySelectorAll('input[type=file]').forEach(function (input) {
        var box = input.parentElement;
        for (var level = 0; box && box !== root && level < 3 && !box.querySelector(':scope > label'); level++) box = box.parentElement;
        if (!box || box === root || !box.querySelector(':scope > label')) return;
        if (box.querySelector('[contenteditable], textarea, select, input:not([type=file])')) return;
        box.setAttribute('data-bna-hidden', '');
      });
    }
  }
  var hideUnusable = safe('menus', function () {
    if (probing) return;
    markUnusable(document.body || document.documentElement);
    document.querySelectorAll('#menu-dropdown').forEach(function (menu) {
      var list = menu.querySelector('.dropdown-menu');
      var key = location.pathname;
      var usable = list ? hasUsableContent(list) : menuUsable[key];
      if (usable === undefined) return probeMenu(menu, key);
      if (usable) menu.removeAttribute('data-bna-hidden'); else menu.setAttribute('data-bna-hidden', '');
    });
  });
  var update = safe('update', function () {
    guardRouter();
    keepLast();
    adaptInsets();
    hideUnusable();
  });
  var updateScheduled = false;
  function scheduleUpdate() {
    if (updateScheduled) return;
    updateScheduled = true;
    requestAnimationFrame(function () { updateScheduled = false; update(); });
  }

  if ({{fonts}}) {
    fontStyle = styleElement('beste-noten-app-fonts');
    fontStyle.textContent = {{fonts}};
  }
  define('__bnaApplyTheme', function (theme) {
    if (typeof theme === 'string') theme = JSON.parse(theme);
    if (!theme || typeof theme.css !== 'string') return;
    document.documentElement.setAttribute('data-bs-theme', theme.dark ? 'dark' : 'light');
    if (!themeStyle) themeStyle = styleElement('beste-noten-app-theme');
    themeStyle.textContent = theme.css;
    keepLast();
  });
  define('__bnaSetBackdrop', function (base64) {
    var url = null;
    var next = null;
    var decoded = Promise.resolve();
    if (typeof base64 === 'string' && base64) {
      var raw = atob(base64);
      var bytes = new Uint8Array(raw.length);
      for (var i = 0; i < raw.length; i++) bytes[i] = raw.charCodeAt(i);
      url = URL.createObjectURL(new Blob([bytes], { type: 'image/jpeg' }));
      next = document.createElement('img');
      next.id = 'beste-noten-app-backdrop';
      next.alt = '';
      next.setAttribute('aria-hidden', 'true');
      next.style.cssText =
        'position:fixed!important;z-index:-1!important;pointer-events:none!important;user-select:none;' +
        'max-width:none!important;max-height:none!important;margin:0!important;padding:0!important;border:0!important;' +
        'left:calc(-1px * var(--bna-backdrop-x,0))!important;top:calc(-1px * var(--bna-backdrop-y,0))!important;' +
        'width:calc(1px * var(--bna-backdrop-w,0))!important;height:calc(1px * var(--bna-backdrop-h,0))!important;';
      next.src = url;
      decoded = next.decode ? next.decode().catch(function () {}) : new Promise(function (resolve) { next.onload = next.onerror = resolve; });
    }
    backdropShown = decoded.then(function () {
      if (backdropElement) backdropElement.remove();
      if (next) document.documentElement.appendChild(next);
      backdropElement = next;
      if (backdropUrl) URL.revokeObjectURL(backdropUrl);
      backdropUrl = url;
      return nextFrames();
    }).then(function () { post('backdropShown', ''); });
  });
  define('__bnaPlaceBackdrop', function (x, y, width, height) {
    var style = document.documentElement.style;
    style.setProperty('--bna-backdrop-x', String(x));
    style.setProperty('--bna-backdrop-y', String(y));
    style.setProperty('--bna-backdrop-w', String(width));
    style.setProperty('--bna-backdrop-h', String(height));
  });
  window.__bnaApplyTheme({{theme}});
  startJournalWithDay();
  post('getTheme', '').then(function (theme) { if (theme) window.__bnaApplyTheme(theme); });

  var health = safe('health', function () {
    var app = document.getElementById('app');
    if (!app || !app.children.length) return 'empty';
    if ((app.innerText || '').trim().length < 20) return 'no visible text';
    var visible = [[0.5, 0.5], [0.5, 0.3], [0.3, 0.5]].some(function (point) {
      return document.elementsFromPoint(innerWidth * point[0], innerHeight * point[1]).some(function (element) {
        return element !== app && app.contains(element);
      });
    });
    return visible ? 'ok' : 'content hidden';
  });
  safe('watch', function () {
    var done = false;
    var settleTimer = null;
    var limitTimer = null;
    function rendered() {
      return document.querySelector('#app .bottom-nav, #app .sidebar, #app .card, #app [class*="container-max-"], #app form');
    }
    var announced = false;
    function announce() {
      if (announced) return;
      announced = true;
      post('rendered', '');
    }
    function finish() {
      if (done) return;
      done = true;
      backdropShown.then(nextFrames).then(function () {
        post('ready', '');
        var attempts = 0;
        (function check() {
          var result = health() || 'failed';
          if (result === 'ok' || ++attempts >= 10) post('health', result); else setTimeout(check, 1000);
        })();
      });
    }
    function settle() {
      announce();
      clearTimeout(settleTimer);
      settleTimer = setTimeout(finish, 300);
      if (!limitTimer) limitTimer = setTimeout(finish, 1500);
    }
    new MutationObserver(function () {
      scheduleUpdate();
      if (!done && rendered()) settle();
    }).observe(document.documentElement, { childList: true, subtree: true });
    document.addEventListener('load', scheduleUpdate, true);
    document.addEventListener('DOMContentLoaded', function () {
      update();
      if (rendered()) settle();
    });
    window.addEventListener('load', function () { setTimeout(finish, 6000); });
  })();
})();
