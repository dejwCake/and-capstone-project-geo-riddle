package sk.dejw.android.georiddles.providers;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Uses the Schematic (https://github.com/SimonVT/schematic) to create a content provider and
 * define URIs for the provider
 */

@ContentProvider(
        authority = RiddleProvider.AUTHORITY,
        database = GeoRiddlesDatabase.class)
public final class RiddleProvider {

    public static final String AUTHORITY = "sk.dejw.android.georiddles.riddles";

    interface Path {
        String RIDDLES = "riddles";
    }

    @TableEndpoint(table = GeoRiddlesDatabase.RIDDLES)
    public static class Riddles {

        @ContentUri(
                path = Path.RIDDLES,
                type = "vnd.android.cursor.dir/riddle",
                defaultSort = RiddleContract.COLUMN_NO + " ASC")
        public static final Uri RIDDLES_URI = Uri.parse("content://" + AUTHORITY + "/riddles");

        @InexactContentUri(
                path = Path.RIDDLES + "/#",
                name = "RIDDLE_ID",
                type = "vnd.android.cursor.item/riddle",
                whereColumn = RiddleContract._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse(RIDDLES_URI + "/" + id);
        }
    }
}