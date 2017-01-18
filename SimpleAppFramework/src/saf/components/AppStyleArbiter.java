package saf.components;

/**
 * This interface serves as a family of type that will initialize
 * the style for some set of controls, like the workspace, for example.
 * 
 * @author Richard McKenna
 * @author ?
 * @version 1.0
 */
public interface AppStyleArbiter {
    // THESE ARE COMMON STYLE CLASSES WE'LL USE

    /**
     *
     */
        public static final String CLASS_BORDERED_PANE = "bordered_pane";

    /**
     *
     */
    public static final String CLASS_HEADING_LABEL = "heading_label";

    /**
     *
     */
    public static final String CLASS_SUBHEADING_LABEL = "subheading_label";

    /**
     *
     */
    public static final String CLASS_PROMPT_LABEL = "prompt_label";

    /**
     *
     */
    public static final String CLASS_PROMPT_TEXT_FIELD = "prompt_text_field";

    /**
     *
     */
    public static final String CLASS_FILE_BUTTON = "file_button";
      
    /**
     *
     */
    public static final String EDIT_TOOL_BAR = "edit_tool_bar";
    
    /**
     *
     */
    public static final String SLIDER_TICKS = "axis-minor-tick-mark";
    
    /**
     *
     */
    static final String SLIDER_TRACK_STYLING = "track";
    /**
     *
     */
    
    static final String SLIDER_THUMB_STYLING = "thumb";
    /**
     *
     */
    public static final String SLIDER_LABEL = "axis";
    
    /**
     *
     */
    public static final String LABEL_STYLING = "label_styling";
    
     /**
     *
     */
    static final String BUTTON_STYLING = "button_styling";
    
     /**
     *
     */
    static final String BUTTON_STYLING_GLOW = "button";
    
    /**
     *
     */
    static final String COLOR_PICKER_STYLING = "text";
    
    /**
     *
     */
    static final String POP_UP_BOX = "pop_up_box_styling";
                 
    /**
     *
     */
    static final String COLUMN_HEADER_BACKGROUND = "column-header";
                     
    /**
     *
     */
    static final String COLUMN_HEADER_LABEL = "label";
    
    /**
     *
     */
    static final String NEW_MAP_DIALOG_LABEL = "new_map_dialog_label_styling";
        
    /**
     *
     */
    static final String PROGRESS_BAR = "track";  
    
    /**
     *
     */
    static final String PROGRESS_BAR_TRACK = "progress-bar";
    
    
    public void initStyle();
}
