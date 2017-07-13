package cn.leo.carousel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        //图片url集合
        List<String> imageList = new ArrayList<>();
        imageList.add("http://i0.hdslb.com/bfs/archive/61f12a6081aeaba02cdc3d7104d6c289575c29c2.png");
        imageList.add("http://i0.hdslb.com/bfs/archive/b360c2f55ed52074dd651efc3ecfcfe0d3ecd531.jpg");
        imageList.add("http://i0.hdslb.com/bfs/archive/a9865892285310909c070d676692ed4edc09ca4c.jpg");
        imageList.add("http://i0.hdslb.com/bfs/archive/7dfcc462d2866ad485a09cd3931e3bead9720652.jpg");

        //获取轮播图控件和指示器控件
        CarouselView carouselView = (CarouselView) findViewById(R.id.carouselView);
        ViewPagerPoint viewPagerPoint = (ViewPagerPoint) findViewById(R.id.vpp);
        //轮播图控件必须自己实现图片加载器加载过程
        carouselView.initImageLoader(new CarouselView.ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String imagePath) {
                Glide.with(MainActivity.this).load(imagePath).centerCrop().into(imageView);
            }
        }).setImageList(imageList);
        //轮播图指示器控件，绑定ViewPager即可，如果是循环的根据循环方式选择参数
        viewPagerPoint.attachViewPager(carouselView.getViewPager(), imageList.size());
    }
}
