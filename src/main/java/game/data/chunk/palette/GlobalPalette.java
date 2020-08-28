package game.data.chunk.palette;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Holds a global palette as introduced in 1.13. These are read from a simple JSON file that is generated by the
 * Minecraft server.jar. More details are in the readme file in the resource folder.
 */
public class GlobalPalette {
    private HashMap<Integer, BlockState> states;
    private HashMap<String, BlockState> nameStates;

    /**
     * Instantiate a global palette using the given Minecraft version.
     * @param version the Minecraft version (e.g. 1.12.2), NOT protocol version
     */
    public GlobalPalette(String version) {
        this(GlobalPalette.class.getClassLoader().getResourceAsStream("blocks-" + version + ".json"));
    }

    /**
     * Instantiate a global palette using the input stream (to a JSON file).
     */
    public GlobalPalette(InputStream input) {
        this.states = new HashMap<>();
        this.nameStates = new HashMap<>();

        // if the file doesn't exist, there is no palette for this version.
        if (input == null) { return; }

        JsonResult map = new Gson().fromJson(new InputStreamReader(input), JsonResult.class);
        map.forEach((name, type) -> type.states.forEach(state -> {
            BlockState s = new BlockState(name, state.id, state.properties);
            states.put(state.id, s);
            nameStates.put(name, s);
        }));
    }

    /**
     * Get a block state from a given index. Used to convert packet palettes to the global palette.
     */
    public BlockState getState(int key) {
        return states.getOrDefault(key, null);
    }

    public BlockState getState(String key) {
        return nameStates.getOrDefault(key, null);
    }

    /**
     * Returns the first state in the palette, used to replace unknown states with air. 
     */
    public BlockState getDefaultState() {
        return states.values().iterator().next();
    }
}

// we need a class to represent this type because of type erasure, otherwise Gson will get angry over casting.
class JsonResult extends HashMap<String, JsonBlockType> { }

// additional classes for inside the JsonResult
class JsonBlockType { ArrayList<JsonBlockState> states; }
class JsonBlockState { int id; HashMap<String, String> properties;}