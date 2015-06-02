package org.aklein.grooid.sample

import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import groovy.android.AndroidBuilder
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT

@CompileStatic
class MainActivity extends AppCompatActivity {
    final static int ID_SETTINGS = Menu.FIRST + 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(createView())
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    protected View createView() {
        new AndroidBuilder().build(this) {
            relativeLayout(width: MATCH_PARENT, height: MATCH_PARENT, padding: [dp(64), dp(16)]) {
                textView(width: MATCH_PARENT, height: dp(20), text: R.string.hello_world)
            }
        } as View
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.add(0, ID_SETTINGS, 100, R.string.action_settings)
        MenuItemCompat.setShowAsAction(item, 0)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.itemId
        if (id == ID_SETTINGS) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
