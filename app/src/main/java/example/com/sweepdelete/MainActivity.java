package example.com.sweepdelete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import example.com.views.SweepDelete;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private List<SweepDelete> sds = new ArrayList<SweepDelete>();
    private List<String> datas=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData() {
        for(int i=0;i<100;++i){
            datas.add("测试："+i);
        }
        lv.setAdapter(new MyListAdapter());
        //当有一个条目的删除按钮被显示时 若滚动listview则该删除按钮自动隐藏
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                closeAll();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initView() {
        lv = (ListView)findViewById(R.id.list);
    }

    class MyListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            if (datas!=null){
                return datas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (datas!=null){
                return datas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh=null;
            if (convertView==null){
                convertView=View.inflate(MainActivity.this,R.layout.listitem,null);
                vh=new ViewHolder();
                convertView.setTag(vh);
                vh.content= (TextView)convertView.findViewById(R.id.content);
                vh.delete= (TextView)convertView.findViewById(R.id.delete);
                vh.sweepDelete= (SweepDelete)convertView.findViewById(R.id.sd);
            }else{
                vh= (ViewHolder)convertView.getTag();
            }
            vh.content.setText(datas.get(position));
            vh.sweepDelete.setSweepListener(new SweepDelete.SweepListener() {
                @Override
                public void isOpened(SweepDelete sweepView, boolean isOpened) {
                    if (isOpened){
                        if (!sds.contains(sweepView)){
                            //若之前有显示的删除按钮 则将其隐藏 即只能有一个删除按钮可以出现
                            closeAll();
                            sds.add(sweepView);
                        }
                    }else{
                        sds.remove(sweepView);
                    }

                }
            });
            vh.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datas.remove(datas.get(position));

                    closeAll();
                    notifyDataSetChanged();

                }
            });
            return convertView;
        }

        class ViewHolder{
            TextView content;
            TextView delete;
            SweepDelete sweepDelete;

    }}

    private void closeAll(){
        ListIterator<SweepDelete> iterator=sds.listIterator();
        while(iterator.hasNext()){
            SweepDelete sweepDelete=iterator.next();
            sweepDelete.close();
        }
    }
}
