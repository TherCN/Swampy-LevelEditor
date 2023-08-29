package thercn.swampy.leveleditor.CustomContent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;
import thercn.swampy.leveleditor.R;

public class MyAdapter extends BaseAdapter {

		private Context context;
		private List<String> items;
		public MyAdapter(Context context, List<String> items) {
				this.context = context;
				this.items = items;
		}
		@Override
		public int getCount() {
				return items.size();
		}
		@Override
		public Object getItem(int position) {
				return items.get(position);
		}
		@Override
		public long getItemId(int position) {
				return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
				View view = LayoutInflater.from(context).inflate(R.layout.property_edit, parent, false);
				TextView textView1 = view.findViewById(R.id.PropertyName);
				TextView textView2 = view.findViewById(R.id.PropertyValue);
				EditText editText1 = view.findViewById(R.id.editPropertyName);
                EditText editText2 = view.findViewById(R.id.editPropertyValue);
				textView1.setText(items.get(position).split(",")[0]);
				textView2.setText(items.get(position).split(",")[1]);
				editText1.setText(items.get(position).split(",")[2]);
                editText2.setText(items.get(position).split(",")[3]);
				return view;
		}


}
