package club.kittenpvp.lintcardboard.mixin.fixture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "club.kittenpvp.lintcardboard.mixin.target.GreetingTarget")
public class GreetingMixin {
    @Overwrite
    public String greet() {
        return "mixed";
    }
}
