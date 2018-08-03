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
        authority = GameProvider.AUTHORITY,
        database = GeoRiddlesDatabase.class)
public final class GameProvider {

    public static final String AUTHORITY = "sk.dejw.android.georiddles.games";

    interface Path {
        String GAMES = "games";
    }

    @TableEndpoint(table = GeoRiddlesDatabase.GAMES)
    public static class Games {

        @ContentUri(
                path = Path.GAMES,
                type = "vnd.android.cursor.dir/game",
                defaultSort = GameContract.COLUMN_UUID + " ASC")
        public static final Uri GAMES_URI = Uri.parse("content://" + AUTHORITY + "/games");

        @InexactContentUri(
                path = Path.GAMES + "/#",
                name = "GAME_ID",
                type = "vnd.android.cursor.item/game",
                whereColumn = GameContract._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse(GAMES_URI + "/" + id);
        }
    }
}