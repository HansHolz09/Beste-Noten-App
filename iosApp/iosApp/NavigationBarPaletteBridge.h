#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface NavigationBarPaletteBridge : NSObject

+ (nullable UIView *)makePaletteWithContentView:(UIView *)contentView
                                preferredHeight:(CGFloat)preferredHeight
    NS_SWIFT_NAME(makePalette(contentView:preferredHeight:));

+ (void)setBottomPalette:(nullable UIView *)palette
         onNavigationItem:(UINavigationItem *)navigationItem
    NS_SWIFT_NAME(setBottomPalette(_:on:));

+ (void)setHeight:(CGFloat)height
        forPalette:(UIView *)palette
    NS_SWIFT_NAME(setHeight(_:forPalette:));

@end

NS_ASSUME_NONNULL_END
