package itis.arezzo.elaborato_fattorini.interfaccia;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import itis.arezzo.elaborato_fattorini.R;
import itis.arezzo.elaborato_fattorini.adapter.ContattiAdapter;
import itis.arezzo.elaborato_fattorini.databinding.ActivityMainBinding;
import itis.arezzo.elaborato_fattorini.interfaccia.contatti.ContattiActivity;
import itis.arezzo.elaborato_fattorini.interfaccia.impostazioni.ImpostazioniActivity;
import itis.arezzo.elaborato_fattorini.menu.ChatsFragment;
import itis.arezzo.elaborato_fattorini.menu.StatusFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUpWithPager(binding.pagina);
        binding.tabs.setupWithViewPager(binding.pagina);
        setSupportActionBar(binding.toolbar);

        binding.pagina.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeButtonIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        changeButtonIcon(0);
    }

    private void setUpWithPager(ViewPager viewPager){
        MainActivity.SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(),"Chat");
        //adapter.addFragment(new StatusFragment(),"Stato");
        viewPager.setAdapter(adapter);
    }

    private static class SectionPagerAdapter extends FragmentPagerAdapter{

        private final List<Fragment> mFragmentList=new ArrayList<>();
        private final List<String> mFragmentTitleList=new ArrayList<>();

        public SectionPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        switch (id){
            //case R.id.menu_cerca : Toast.makeText(MainActivity.this, "Azione cerca", Toast.LENGTH_LONG).show(); break;
//
            case R.id.menu_nuovo_gruppo:  Toast.makeText(MainActivity.this, "Gruppo non implementato", Toast.LENGTH_LONG).show(); break;
//
            //case R.id.menu_nuovo_broadcast:  Toast.makeText(MainActivity.this, "Azione broadcast", Toast.LENGTH_LONG).show(); break;
//
            //case R.id.menu_messaggi_importanti:  Toast.makeText(MainActivity.this, "Azione importanti", Toast.LENGTH_LONG).show(); break;

            case R.id.menu_impostazioni:  startActivity(new Intent(MainActivity.this, ImpostazioniActivity.class)); break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeButtonIcon(final int i){
        binding.bottone.hide();

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                switch (i){
                    case 0 :
                        binding.bottone.setImageDrawable(getDrawable(R.drawable.ic_baseline_chat));
                        binding.bottone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(MainActivity.this, ContattiActivity.class));
                            }
                        });
                        break;
                    case 1 :
                        binding.bottone.setImageDrawable(getDrawable(R.drawable.ic_baseline_camera));
                        break;
                }
                binding.bottone.show();
            }
        },400);

    }
}