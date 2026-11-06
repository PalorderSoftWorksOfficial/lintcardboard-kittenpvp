package club.kittenpvp.lintcardboard.mixin.fixture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "club.kittenpvp.lintcardboard.mixin.target.FarewellTarget")
public class BrokenMixin {
    @Inject(method = "methodThatDoesNotExist", at = @At("HEAD"), cancellable = true, require = 1)
    private void lintcardboard$broken(CallbackInfoReturnable<String> info) {
        info.setReturnValue("broken");
    }
}
