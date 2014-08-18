/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package edu.arizona.biosemantics.oto2.theme.client.sliced.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import edu.arizona.biosemantics.oto2.theme.client.base.toolbar.Css3ToolBarAppearance;

/**
 *
 */
public class SlicedToolBarAppearance extends Css3ToolBarAppearance {

  public interface SlicedToolBarResources extends Css3ToolBarResources {
    @Source({"com/sencha/gxt/theme/base/client/container/BoxLayout.css", "edu/arizona/biosemantics/oto2/theme/client/base/container/Css3HBoxLayoutContainer.css", "SlicedToolBar.css"})
    @Override
    Css3ToolBarStyle style();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource background();
  }

  public SlicedToolBarAppearance() {
    this(GWT.<SlicedToolBarResources>create(SlicedToolBarResources.class));
  }

  public SlicedToolBarAppearance(SlicedToolBarResources resources) {
    super(resources);
  }
}
