#import "NavigationBarPaletteBridge.h"
#import <objc/message.h>
#import <objc/runtime.h>

@implementation NavigationBarPaletteBridge

// Using private Api to achieve a calendar like behaviour for the tab bar
// It's okay as long as the app isn't going to be published and the api exists

+ (nullable UIView *)makePaletteWithContentView:(UIView *)contentView
                                preferredHeight:(CGFloat)preferredHeight {
    Class paletteClass = NSClassFromString(@"_UINavigationBarPalette");
    SEL initializer = NSSelectorFromString(@"initWithContentView:");
    if (paletteClass == Nil || ![paletteClass instancesRespondToSelector:initializer]) {
        return nil;
    }

    id allocatedPalette = ((id (*)(id, SEL))objc_msgSend)(paletteClass, sel_registerName("alloc"));
    UIView *palette = ((id (*)(id, SEL, id))objc_msgSend)(allocatedPalette, initializer, contentView);

    SEL setPreferredHeight = NSSelectorFromString(@"setPreferredHeight:");
    if ([palette respondsToSelector:setPreferredHeight]) {
        ((void (*)(id, SEL, CGFloat))objc_msgSend)(palette, setPreferredHeight, preferredHeight);
    }

    SEL setMinimumHeight = NSSelectorFromString(@"setMinimumHeight:");
    if ([palette respondsToSelector:setMinimumHeight]) {
        ((void (*)(id, SEL, CGFloat))objc_msgSend)(palette, setMinimumHeight, preferredHeight);
    }

    SEL setPinned = NSSelectorFromString(@"setPinned:");
    if ([palette respondsToSelector:setPinned]) {
        ((void (*)(id, SEL, BOOL))objc_msgSend)(palette, setPinned, YES);
    }

    return palette;
}

+ (void)setBottomPalette:(nullable UIView *)palette
         onNavigationItem:(UINavigationItem *)navigationItem {
    SEL setter = NSSelectorFromString(@"_setBottomPalette:");
    if ([navigationItem respondsToSelector:setter]) {
        ((void (*)(id, SEL, id))objc_msgSend)(navigationItem, setter, palette);
    }
}

+ (void)setHeight:(CGFloat)height forPalette:(UIView *)palette {
    SEL setPreferredHeight = NSSelectorFromString(@"setPreferredHeight:");
    if ([palette respondsToSelector:setPreferredHeight]) {
        ((void (*)(id, SEL, CGFloat))objc_msgSend)(palette, setPreferredHeight, height);
    }

    SEL setMinimumHeight = NSSelectorFromString(@"setMinimumHeight:");
    if ([palette respondsToSelector:setMinimumHeight]) {
        ((void (*)(id, SEL, CGFloat))objc_msgSend)(palette, setMinimumHeight, height);
    }
}

@end
