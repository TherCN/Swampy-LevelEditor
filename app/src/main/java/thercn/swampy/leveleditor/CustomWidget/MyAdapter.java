package thercn.swampy.leveleditor.CustomWidget;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import thercn.swampy.leveleditor.R;
import android.widget.ArrayAdapter;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private String[][] mObjectProperties;

    public MyAdapter(Context context, String[][] objectProperties) {
        mContext = context;
        mObjectProperties = objectProperties;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.property_edit, parent, false);
            holder = new ViewHolder();
            holder.propertyNameEdit = convertView.findViewById(R.id.editPropertyName);
            holder.propertyValueEdit = convertView.findViewById(R.id.editPropertyValue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.propertyNameEdit.setText(mObjectProperties[0][position]);
        holder.propertyValueEdit.setText(mObjectProperties[1][position]);
				holder.propertyNameEdit.setTextSize(10);
				holder.propertyValueEdit.setTextSize(10);

        return convertView;
    }

    private static class ViewHolder {
        EditText propertyNameEdit;
        EditText propertyValueEdit;
    }
}
