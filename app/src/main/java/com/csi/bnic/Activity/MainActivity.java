package com.csi.bnic.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csi.bnic.Insurance.Omp.ActivityOmpEntry;
import com.csi.bnic.Insurance.Web.ActivityWeb;
import com.csi.bnic.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.sslwireless.sslcommerzlibrary.model.response.TransactionInfoModel;
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.TransactionResponseListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TransactionResponseListener {
    LinearLayout linearLayoutMotorInsurance,linearLayoutFireInsurance,linearLayoutMarineInsurance,linearLayoutOMP;
    Context context = this;
    RelativeLayout relativeLayoutImage;
    Animation animationFade, animationLeftEnter,animationRightEnter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUI();
        animationFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        animationLeftEnter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_enter);
        animationRightEnter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_enter);
        relativeLayoutImage.setAnimation(animationFade);
        linearLayoutMotorInsurance.setAnimation(animationRightEnter);
        linearLayoutFireInsurance.setAnimation(animationLeftEnter);
        linearLayoutMarineInsurance.setAnimation(animationRightEnter);
        linearLayoutOMP.setAnimation(animationLeftEnter);
        linearLayoutMotorInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ActivityCategory.class));
            }
        });
        linearLayoutFireInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,ActivityFireInsurance.class));
                dialog();
            }
        });
        linearLayoutMarineInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,ActivityMarineInsurance.class));
                dialog();
            }
        });
        linearLayoutOMP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOMP();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void dialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.a);
        text.setText("This feature will be available soon!");
        imageView.startAnimation(animation);

        Button buttonNo = (Button) dialog.findViewById(R.id.buttonNo);
        Button buttonYes = (Button) dialog.findViewById(R.id.buttonYes);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 10000);
        dialog.show();
    }

    private void initUI() {
        linearLayoutMotorInsurance = (LinearLayout) findViewById(R.id.linearMotorInsurance);
        linearLayoutFireInsurance = (LinearLayout) findViewById(R.id.linearFireInsurance);
        linearLayoutMarineInsurance = (LinearLayout) findViewById(R.id.linearMarineInsurance);
        linearLayoutOMP = (LinearLayout) findViewById(R.id.linearOmpInsurance);
        relativeLayoutImage = (RelativeLayout) findViewById(R.id.collapsing);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about_us) {
            Intent intent = new Intent(MainActivity.this,ActivityWeb.class);
            intent.putExtra("passValue",3);
            startActivity(intent);
        } else if (id == R.id.nav_company_profile) {
            Intent intent = new Intent(MainActivity.this,ActivityWeb.class);
            intent.putExtra("passValue",4);
            startActivity(intent);
        } else if (id == R.id.nav_directors) {
            Intent intent = new Intent(MainActivity.this,ActivityWeb.class);
            intent.putExtra("passValue",5);
            startActivity(intent);
        } else if (id == R.id.nav_management) {
            Intent intent = new Intent(MainActivity.this,ActivityWeb.class);
            intent.putExtra("passValue",6);
            startActivity(intent);
        } else if (id == R.id.nav_branches) {
            Intent intent = new Intent(MainActivity.this,ActivityWeb.class);
            intent.putExtra("passValue",7);
            startActivity(intent);
        } else if (id == R.id.nav_claim_info) {
            Intent intent = new Intent(MainActivity.this,ActivityWeb.class);
            intent.putExtra("passValue",8);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void transactionSuccess(TransactionInfoModel transactionInfoModel) {

    }

    @Override
    public void transactionFail(String s) {

    }

    @Override
    public void merchantValidationError(String s) {

    }

    private void dialogOMP() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.a);
        text.setText(getString(R.string.OMP_dialog));
        imageView.startAnimation(animation);

        Button buttonNo = (Button) dialog.findViewById(R.id.buttonNo);
        Button buttonYes = (Button) dialog.findViewById(R.id.buttonYes);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ActivityOmpEntry.class));
                dialog.dismiss();
            }
        });

        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 10000);
        dialog.show();
    }

}
