package thercn.swampy.leveleditor;

import android.app.Activity;
import android.os.Bundle;
import thercn.swampy.leveleditor.CustomContent.ImageEditorView;

public class EditImage extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_image);
        ImageEditorView image_editor = findViewById(R.id.image_editor_view);
		
    }
    
}
