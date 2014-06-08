/**
 * Copyright (C) 2011 Whisper Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.thoughtcrime.securesms;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import org.thoughtcrime.securesms.util.DynamicLanguage;
import org.thoughtcrime.securesms.util.DynamicTheme;
import org.whispersystems.textsecure.crypto.MasterSecret;

public class BlackListActivity extends SherlockFragmentActivity {

    private final DynamicTheme    dynamicTheme    = new DynamicTheme();
    private final DynamicLanguage dynamicLanguage = new DynamicLanguage();
    private MasterSecret masterSecret;

    @Override
    public void onCreate(Bundle bundle) {
        dynamicTheme.onCreate(this);
        dynamicLanguage.onCreate(this);
        super.onCreate(bundle);
        setContentView(R.layout.blacklist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        dynamicTheme.onResume(this);
        dynamicLanguage.onResume(this);
        getSupportActionBar().setTitle(R.string.AndroidManifest__blacklist);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_block_contact:       openSingleContactSelection();   return true;
        }

        return false;
    }

    private void openSingleContactSelection() {
        Intent intent = new Intent(this, NewBlockContactActivity.class);
        intent.putExtra(NewBlockContactActivity.MASTER_SECRET_EXTRA, masterSecret);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getSupportMenuInflater();
        menu.clear();

        inflater.inflate(R.menu.blacklist_options, menu);

        super.onPrepareOptionsMenu(menu);
        return true;
    }

}