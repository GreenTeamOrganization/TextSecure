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

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.whispersystems.textsecure.crypto.IdentityKey;
import org.thoughtcrime.securesms.database.BlackListDatabase;
import org.thoughtcrime.securesms.recipients.Recipient;
import org.thoughtcrime.securesms.recipients.Recipients;

/**
 * List item view for displaying blacklist.
 *
 * @author GreenTeam
 *
 */
public class BlackListView extends RelativeLayout
        implements Recipient.RecipientModifiedListener
{

    private TextView          blockNumber;
    private TextView          fingerprint;
    private QuickContactBadge contactBadge;
    private ImageView         contactImage;

    private Recipients  recipients;
    private IdentityKey identityKey;

    private final Handler handler = new Handler();

    public BlackListView(Context context) {
        super(context);
    }

    public BlackListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onFinishInflate() {
        this.blockNumber = (TextView)findViewById(R.id.identity_name);
        this.fingerprint  = (TextView)findViewById(R.id.fingerprint);
        this.contactBadge = (QuickContactBadge)findViewById(R.id.contact_photo_badge);
        this.contactImage = (ImageView)findViewById(R.id.contact_photo_image);

        if (isBadgeEnabled()) {
            this.contactBadge.setVisibility(View.VISIBLE);
            this.contactImage.setVisibility(View.GONE);
        } else {
            this.contactBadge.setVisibility(View.GONE);
            this.contactImage.setVisibility(View.VISIBLE);
        }
    }

    public void set(BlackListDatabase.BlackList blacklist) {
        this.recipients  = blacklist.getRecipients();

        this.recipients.addListener(this);

        blockNumber.setText(recipients.toShortString());

        contactBadge.setImageBitmap(recipients.getPrimaryRecipient().getContactPhoto());
        contactBadge.assignContactFromPhone(recipients.getPrimaryRecipient().getNumber(), true);
        contactImage.setImageBitmap(recipients.getPrimaryRecipient().getContactPhoto());
    }

    public IdentityKey getIdentityKey() {
        return this.identityKey;
    }

    public Recipient getRecipient() {
        return this.recipients.getPrimaryRecipient();
    }

    private boolean isBadgeEnabled() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    @Override
    public void onModified(Recipient recipient) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                BlackListView.this.blockNumber.setText(recipients.toShortString());
            }
        });
    }
}
