package thercn.swampy.leveleditor.CustomWidget;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import thercn.swampy.leveleditor.LevelManager.LevelXMLParser;
import thercn.swampy.leveleditor.R;
import thercn.swampy.leveleditor.LevelManager.LevelEditor;
import thercn.swampy.leveleditor.AppUtils.AppLog;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private String[][] mObjectProperties;
	String currentObject;
	int currentObjectId;

    public MyAdapter(Context context, String[][] objectProperties) {
        mContext = context;
        mObjectProperties = objectProperties;
    }
	
	public void setCurrentObject(String name){
		this.currentObject = name;
		this.currentObjectId = LevelEditor.currentLevel.getObjectItemId(name);
	}
	public void setCurrentObject(int id){
		this.currentObjectId = id;
	}
    @Override
    public int getCount() {
        return mObjectProperties[0].length;
    }

	@Override
	public Object getItem(int position) {
		return "a";
	}

    @Override
    public long getItemId(int position) {
        return 0;
    }

	
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
		final int item = position;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.property_edit, parent, false);
            holder = new ViewHolder();
            holder.propertyNameEdit = convertView.findViewById(R.id.editPropertyName);
            holder.propertyValueEdit = convertView.findViewById(R.id.editPropertyValue);
			holder.update = convertView.findViewById(R.id.update);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.propertyNameEdit.setText(mObjectProperties[0][position]);
        holder.propertyValueEdit.setText(mObjectProperties[1][position]);
		holder.propertyNameEdit.setTextSize(10);
		holder.propertyValueEdit.setTextSize(10);

		holder.update.setEnabled(false);
		holder.propertyValueEdit.addTextChangedListener(new TextWatcher(){

				@Override
				public void beforeTextChanged(CharSequence s, int start,int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					holder.update.setEnabled(true);
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
		holder.update.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					if (holder.propertyNameEdit.getText().toString().isEmpty() ||
					    holder.propertyValueEdit.getText().toString().isEmpty())
					{
						return;
					}
					LevelEditor.currentLevel.setPropertyValue(currentObjectId,
															  holder.propertyNameEdit.getText().toString(),
										 					  holder.propertyValueEdit.getText().toString());
					AppLog.WriteLog(currentObjectId + " " +
									holder.propertyNameEdit.getText().toString() + " " +
									holder.propertyValueEdit.getText().toString());
					holder.update.setEnabled(false);
				}
			});
        return convertView;
    }

    private static class ViewHolder {
        EditText propertyNameEdit;
        EditText propertyValueEdit;
		Button update;
    }
}
