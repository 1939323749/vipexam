package app.xlei.vipexam.tile

import android.content.Intent
import android.service.quicksettings.TileService
import app.xlei.vipexam.glance.WordActivity


class VipExamQSTileService : TileService() {

    // Called when the user adds your tile.
    override fun onTileAdded() {
        super.onTileAdded()
    }

    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening()
    }

    // Called when your app can no longer update your tile.
    override fun onStopListening() {
        super.onStopListening()
    }

    // Called when the user taps on your tile in an active or inactive state.
    var counter = 0
    override fun onClick() {
        super.onClick()
        val intent = Intent(this.applicationContext, WordActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityAndCollapse(intent)
//        counter++
//        qsTile.state = if (counter % 2 == 0) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
//        qsTile.label = "Clicked $counter times"
//        qsTile.contentDescription = qsTile.label
//        qsTile.updateTile()
    }

    // Called when the user removes your tile.
    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}