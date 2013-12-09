package unittest;

import texture.TextureType;
import junit.framework.TestCase;

/**
 * This set of tests makes sure the TextureType enum works properly.
 * 
 * @author Adi
 */
public class TextureTypeTest extends TestCase {
    /**
     * Test that the default texture type is returned for filenames that haven't been registered.
     */
    public void testDefaultTextureType() {
        String fakeFileName = "asdfsdflsdfsdf.png";
        TextureType type = TextureType.getTextureTypeFromFilename(fakeFileName);

        assertEquals(type, TextureType.getDefaultTexture());
    }
    
    /**
     * Tests that a valid texture type is returned for registered filenames.
     */
    public void testRegisteredTextureType() {
        String fileName = "fur_hair.png";
        TextureType type = TextureType.getTextureTypeFromFilename(fileName);

        assertEquals(type, TextureType.BUNNY_FUR);    
    }
}