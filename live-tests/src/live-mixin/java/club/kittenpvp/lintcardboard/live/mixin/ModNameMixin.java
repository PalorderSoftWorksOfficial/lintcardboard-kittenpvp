package club.kittenpvp.lintcardboard.live.mixin;

import club.kittenpvp.lintcardboard.brand.ServerBrand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "net.minecraft.server.MinecraftServer")
public class ModNameMixin {
    @Overwrite
    public String getServerModName() {
        return ServerBrand.NAME;
    }
}
