package club.kittenpvp.lintcardboard.brand;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ServerBrandTest {
    @Test
    void identityIsLintCardboard() {
        assertEquals("lintcardboard", ServerBrand.ID);
        assertEquals("LintCardboard", ServerBrand.NAME);
        assertFalse(ServerBrand.VERSION.isBlank());
    }

    @Test
    void startupLineBrandsServerLikeLeaf() {
        assertEquals(
            "This server is running LintCardboard version 26.2-135",
            ServerBrand.startupLine("26.2-135")
        );
    }
}
