package club.kittenpvp.lintcardboard.mixin;

import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;

public class MixinContainerHandle extends ContainerHandleVirtual {
    public MixinContainerHandle(String name) {
        super(name);
    }
}
