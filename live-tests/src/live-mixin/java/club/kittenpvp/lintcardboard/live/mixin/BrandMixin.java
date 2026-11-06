package club.kittenpvp.lintcardboard.live.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "io.papermc.paper.ServerBuildInfoImpl")
public class BrandMixin {
    @Overwrite
    public String brandName() {
        return "(lintcardboard) Paper";
    }
}
