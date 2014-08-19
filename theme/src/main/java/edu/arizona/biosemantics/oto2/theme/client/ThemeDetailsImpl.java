package edu.arizona.biosemantics.oto2.theme.client;

public class ThemeDetailsImpl implements ThemeDetails {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ToolBarDetails toolbar() {
  return new edu.arizona.biosemantics.oto2.theme.client.ToolBarDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ToolBarDetails.LabelToolItemDetails labelItem() {
  return new edu.arizona.biosemantics.oto2.theme.client.ToolBarDetails.LabelToolItemDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String lineHeight() {
  return "17px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#ffffff 0%, #ffffff 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ButtonDetails buttonOverride() {
  return new edu.arizona.biosemantics.oto2.theme.client.ButtonDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String mediumFontSize() {
  return "14";
  }

  @Override
  public java.lang.String overGradient() {
  return "#EDEDED 0%, #EBEBEB 50%, #DFDFDF 51%, #EBEBEB 100%";
  }

  @Override
  public java.lang.String gradient() {
  return "#F6F6F6 0%, #F5F5F5 50%, #E8E8E8 51%, #F5F5F5 100%";
  }

  @Override
  public java.lang.String largeFontSize() {
  return "16";
  }

  @Override
  public java.lang.String smallLineHeight() {
  return "18";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails radiusMinusBorderWidth() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String largeLineHeight() {
  return "32";
  }

  @Override
  public java.lang.String pressedGradient() {
  return "#E1E1E1 0%, #D5D5D5 50%, #E1E1E1 51%, #E4E4E4 100%";
  }

  @Override
  public java.lang.String arrowColor() {
  return "#666666";
  }

  @Override
  public java.lang.String smallFontSize() {
  return "12";
  }

  @Override
  public java.lang.String mediumLineHeight() {
  return "24";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#cecece";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails font() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 4;
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ColorPaletteDetails colorpalette() {
  return new edu.arizona.biosemantics.oto2.theme.client.ColorPaletteDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public java.lang.String selectedBackgroundColor() {
  return "#e6e6e6";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails itemPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails itemBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails selectedBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#8bb8f3";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int itemSize() {
  return 16;
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.AccordionLayoutDetails accordionLayout() {
  return new edu.arizona.biosemantics.oto2.theme.client.AccordionLayoutDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#DFEAF2";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerGradient() {
  return "#DFEAF2 0%, #DFEAF2 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails font() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 8;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 8;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FramedPanelDetails framedPanel() {
  return new edu.arizona.biosemantics.oto2.theme.client.FramedPanelDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#279446";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public java.lang.String color() {
  return "#279446";
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerGradient() {
  return "#279446 0%, #279446 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails font() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "arial,helvetica,verdana,sans-serif";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 4;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails radiusMinusBorderWidth() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 6;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderLayoutDetails borderLayout() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderLayoutDetails() {

  @Override
  public java.lang.String panelBackgroundColor() {
  return "#279446";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails collapsePanelBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#279446";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.WindowDetails window() {
  return new edu.arizona.biosemantics.oto2.theme.client.WindowDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#279446";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public java.lang.String color() {
  return "#3291d6";
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerGradient() {
  return "#3291d6 0%, #3291d6 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails font() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "arial,helvetica,verdana,sans-serif";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 4;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails radiusMinusBorderWidth() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 6;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.SplitBarDetails splitbar() {
  return new edu.arizona.biosemantics.oto2.theme.client.SplitBarDetails() {

  @Override
  public int handleWidth() {
  return 8;
  }

  @Override
  public java.lang.String dragColor() {
  return "#B4B4B4";
  }

  @Override
  public int handleHeight() {
  return 48;
  }

  @Override
  public double handleOpacity() {
  return 0.5;
  }
    };
  }

  @Override
  public java.lang.String disabledTextColor() {
  return "";
  }

  @Override
  public java.lang.String borderColor() {
  return "#279446";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ToolIconDetails tools() {
  return new edu.arizona.biosemantics.oto2.theme.client.ToolIconDetails() {

  @Override
  public java.lang.String warningColor() {
  return "#D94E37";
  }

  @Override
  public java.lang.String primaryClickColor() {
  return "#c9e4d0";
  }

  @Override
  public java.lang.String primaryColor() {
  return "#93c9a2";
  }

  @Override
  public java.lang.String primaryOverColor() {
  return "#bedec7";
  }

  @Override
  public java.lang.String allowColor() {
  return "#C6E38A";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FieldSetDetails fieldset() {
  return new edu.arizona.biosemantics.oto2.theme.client.FieldSetDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#b5b8c8";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.TreeDetails tree() {
  return new edu.arizona.biosemantics.oto2.theme.client.TreeDetails() {

  @Override
  public java.lang.String dropBackgroundColor() {
  return "#e2eff8";
  }

  @Override
  public java.lang.String selectedBackgroundColor() {
  return "#c1ddf1";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String overBackgroundColor() {
  return "#e2eff8";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails nodePadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails iconMargin() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 4;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemHeight() {
  return "25px";
  }

  @Override
  public java.lang.String dragOverBackgroundColor() {
  return "#e2eff8";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ButtonGroupDetails buttonGroup() {
  return new edu.arizona.biosemantics.oto2.theme.client.ButtonGroupDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails bodyPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 4;
  }

  @Override
  public int right() {
  return 4;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 4;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public java.lang.String color() {
  return "#dfeaf2";
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerGradient() {
  return "#dfeaf2 0%, #dfeaf2 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails font() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String bodyBackgroundColor() {
  return "#FFFFFF";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.StatusProxyDetails statusproxy() {
  return new edu.arizona.biosemantics.oto2.theme.client.StatusProxyDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public double opacity() {
  return 0.85;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#dddddd #bbbbbb #bbbbbb #dddddd";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.MaskDetails mask() {
  return new edu.arizona.biosemantics.oto2.theme.client.MaskDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.MaskDetails.BoxDetails box() {
  return new edu.arizona.biosemantics.oto2.theme.client.MaskDetails.BoxDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#e5e5e5";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String borderStyle() {
  return "none";
  }

  @Override
  public int borderWidth() {
  return 0;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails textPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 21;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String borderColor() {
  return "";
  }

  @Override
  public int radiusMinusBorderWidth() {
  return 3;
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public java.lang.String loadingImagePosition() {
  return "center 0";
  }
    };
  }

  @Override
  public double opacity() {
  return 0.7;
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.MessageBoxDetails messagebox() {
  return new edu.arizona.biosemantics.oto2.theme.client.MessageBoxDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails iconPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails bodyPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails messagePadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.DatePickerDetails datePicker() {
  return new edu.arizona.biosemantics.oto2.theme.client.DatePickerDetails() {

  @Override
  public java.lang.String itemOverColor() {
  return "#000000";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails dayPreviousText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#bfbfbf";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails buttonMargin() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemOverBackgroundColor() {
  return "#eaf3fa";
  }

  @Override
  public java.lang.String itemSelectedBackgroundColor() {
  return "#d6e8f6";
  }

  @Override
  public java.lang.String dayOfWeekBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails itemSelectedText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails dayOfWeekPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 9;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails todayBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#8b0000";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails footerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails dayBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String dayOfWeekLineHeight() {
  return "24px";
  }

  @Override
  public java.lang.String dayDisabledBackgroundColor() {
  return "#eeeeee";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails itemSelectedBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#3892d3";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 8;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 8;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails dayNextText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#bfbfbf";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public java.lang.String dayPreviousBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails dayOfWeekText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String width() {
  return "212px";
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails dayPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails dayDisabledText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#808080";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String footerBackgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public java.lang.String dayNextBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails headerText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#3892d3";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String dayLineHeight() {
  return "23px";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerTextPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails dayText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ProgressBarDetails progressbar() {
  return new edu.arizona.biosemantics.oto2.theme.client.ProgressBarDetails() {

  @Override
  public java.lang.String backgroundGradient() {
  return "";
  }

  @Override
  public java.lang.String barTextColor() {
  return "#666666";
  }

  @Override
  public java.lang.String barGradient() {
  return "#c1ddf1 0%, #c1ddf1 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails textPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String textAlign() {
  return "center";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails barBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.InfoDetails info() {
  return new edu.arizona.biosemantics.oto2.theme.client.InfoDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails margin() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 7;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 7;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public double opacity() {
  return 1.0;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails messagePadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public java.lang.String color() {
  return "#cccccc";
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails headerText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#555555";
  }

  @Override
  public java.lang.String size() {
  return "15px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 8;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails radiusMinusBorderWidth() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 6;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 6;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails messageText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#555555";
  }

  @Override
  public java.lang.String size() {
  return "14px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 8;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.MenuDetails menu() {
  return new edu.arizona.biosemantics.oto2.theme.client.MenuDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails activeItemBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails activeItemText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails itemPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemLineHeight() {
  return "24px";
  }

  @Override
  public java.lang.String gradient() {
  return "#ffffff 0%, #ffffff 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.MenuDetails.HeaderItemDetails header() {
  return new edu.arizona.biosemantics.oto2.theme.client.MenuDetails.HeaderItemDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#D6E3F2";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails itemPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemLineHeight() {
  return "13px";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#99bbe8";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails itemText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#15428b";
  }

  @Override
  public java.lang.String size() {
  return "10px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "tahoma,arial,verdana,sans-serif";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String activeItemGradient() {
  return "#d6e8f6 0%, #d6e8f6 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.MenuDetails.MenuBarDetails bar() {
  return new edu.arizona.biosemantics.oto2.theme.client.MenuDetails.MenuBarDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails hoverItemText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails activeItemBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails activeItemText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String hoverItemGradient() {
  return "#d6e8f6 0%, #d6e8f6 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails itemPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public int right() {
  return 8;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 8;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails hoverItemBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String itemLineHeight() {
  return "24px";
  }

  @Override
  public java.lang.String activeItemGradient() {
  return "#d6e8f6 0%, #d6e8f6 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails itemText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#ffffff 0%, #ffffff 100%";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.MenuDetails.MenuSeparatorDetails separator() {
  return new edu.arizona.biosemantics.oto2.theme.client.MenuDetails.MenuSeparatorDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails margin() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int height() {
  return 1;
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails itemText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String lastGradientColor() {
  return "#ffffff";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.StatusDetails status() {
  return new edu.arizona.biosemantics.oto2.theme.client.StatusDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 5;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#cccccc #d9d9d9 #d9d9d9";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String lineHeight() {
  return "16px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FieldDetails field() {
  return new edu.arizona.biosemantics.oto2.theme.client.FieldDetails() {

  @Override
  public int invalidBorderWidth() {
  return 1;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String borderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String borderColor() {
  return "#c0c0c0";
  }

  @Override
  public java.lang.String focusBorderColor() {
  return "#3892d3";
  }

  @Override
  public java.lang.String invalidBorderColor() {
  return "#D94E37";
  }

  @Override
  public java.lang.String emptyTextColor() {
  return "#808080";
  }

  @Override
  public java.lang.String invalidBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.SliderDetails slider() {
  return new edu.arizona.biosemantics.oto2.theme.client.SliderDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails trackBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#d4d4d4";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int thumbRadius() {
  return 8;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails thumbBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#777777";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String trackBackgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public int thumbHeight() {
  return 15;
  }

  @Override
  public int trackRadius() {
  return 4;
  }

  @Override
  public java.lang.String thumbBackgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public int thumbWidth() {
  return 15;
  }

  @Override
  public int trackHeight() {
  return 8;
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FieldDetails.FieldLabelDetails sideLabel() {
  return new edu.arizona.biosemantics.oto2.theme.client.FieldDetails.FieldLabelDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails fieldPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String textAlign() {
  return "left";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails labelPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 5;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public int borderWidth() {
  return 1;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FieldDetails.FieldLabelDetails topLabel() {
  return new edu.arizona.biosemantics.oto2.theme.client.FieldDetails.FieldLabelDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 5;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails fieldPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String textAlign() {
  return "left";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails labelPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 6;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public java.lang.String lineHeight() {
  return "18px";
  }

  @Override
  public int height() {
  return 24;
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.GridDetails grid() {
  return new edu.arizona.biosemantics.oto2.theme.client.GridDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.GridDetails.ColumnHeaderDetails columnHeader() {
  return new edu.arizona.biosemantics.oto2.theme.client.GridDetails.ColumnHeaderDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails menuHoverBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String menuGradient() {
  return "#f5f5f5 0%, #f5f5f5 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 7;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 7;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String borderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String borderColor() {
  return "#c0c0c0";
  }

  @Override
  public java.lang.String overGradient() {
  return "#eef6fb 0%, #eef6fb 100%";
  }

  @Override
  public java.lang.String gradient() {
  return "#f5f5f5 0%, #f5f5f5 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String menuActiveGradient() {
  return "#DFEAF2 0%, #DFEAF2 100%";
  }

  @Override
  public int borderWidth() {
  return 1;
  }

  @Override
  public int menuButtonWidth() {
  return 18;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails menuBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String lineHeight() {
  return "15px";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails menuActiveBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String menuHoverGradient() {
  return "#eef6fb 0%, #eef6fb 100%";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.GridDetails.GroupDetails group() {
  return new edu.arizona.biosemantics.oto2.theme.client.GridDetails.GroupDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#f5f5f5";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#666666";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 8;
  }

  @Override
  public int right() {
  return 4;
  }

  @Override
  public int bottom() {
  return 8;
  }

  @Override
  public int left() {
  return 4;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#c0c0c0";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int iconSpacing() {
  return 17;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.GridDetails.GroupDetails.SummaryDetails summary() {
  return new edu.arizona.biosemantics.oto2.theme.client.GridDetails.GroupDetails.SummaryDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }
    };
  }
    };
  }

  @Override
  public int cellBorderWidth() {
  return 1;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails cellPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String cellOverHBorderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String cellBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public java.lang.String cellSelectedVBorderColor() {
  return "#ededed";
  }

  @Override
  public java.lang.String specialColumnGradientSelected() {
  return "";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.GridDetails.RowEditorDetails rowEditor() {
  return new edu.arizona.biosemantics.oto2.theme.client.GridDetails.RowEditorDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#DFEAF2";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.GridDetails.FooterDetails footer() {
  return new edu.arizona.biosemantics.oto2.theme.client.GridDetails.FooterDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails cellBorder() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#ededed";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }
    };
  }

  @Override
  public java.lang.String cellSelectedVBorderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String specialColumnGradient() {
  return "";
  }

  @Override
  public java.lang.String cellVBorderColor() {
  return "#ededed";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.GridDetails.RowNumbererDetails rowNumberer() {
  return new edu.arizona.biosemantics.oto2.theme.client.GridDetails.RowNumbererDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 5;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public java.lang.String cellLineHeight() {
  return "15px";
  }

  @Override
  public java.lang.String cellSelectedHBorderColor() {
  return "#e2eff8";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails cellText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public java.lang.String cellHBorderColor() {
  return "#ededed";
  }

  @Override
  public java.lang.String cellOverHBorderColor() {
  return "#e2eff8";
  }

  @Override
  public java.lang.String cellSelectedHBorderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String cellOverBackgroundColor() {
  return "#e2eff8";
  }

  @Override
  public java.lang.String cellOverVBorderStyle() {
  return "solid";
  }

  @Override
  public java.lang.String cellSelectedBackgroundColor() {
  return "#c1ddf1";
  }

  @Override
  public java.lang.String cellAltBackgroundColor() {
  return "#fafafa";
  }

  @Override
  public java.lang.String cellOverVBorderColor() {
  return "#ededed";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.TipDetails tip() {
  return new edu.arizona.biosemantics.oto2.theme.client.TipDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails margin() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#eaf3fa";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public double opacity() {
  return 1.0;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails messagePadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails headerText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails radiusMinusBorderWidth() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails messageText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.PanelDetails panel() {
  return new edu.arizona.biosemantics.oto2.theme.client.PanelDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerBackgroundColor() {
  return "#279446";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String headerGradient() {
  return "#279446 0%, #279446 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails font() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "arial,helvetica,verdana,sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.TabDetails tabs() {
  return new edu.arizona.biosemantics.oto2.theme.client.TabDetails() {

  @Override
  public java.lang.String tabItemBorderLeft() {
  return "none";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 10;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 10;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails tabTextPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 10;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 10;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails headingText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public java.lang.String borderColor() {
  return "#279446";
  }

  @Override
  public int scrollerWidth() {
  return 18;
  }

  @Override
  public java.lang.String tabStripGradient() {
  return "#279446 0%, #279446 100%";
  }

  @Override
  public java.lang.String gradient() {
  return "#a8d04d 0%, #a8d04d 100%";
  }

  @Override
  public int tabHeight() {
  return 31;
  }

  @Override
  public java.lang.String tabBarBorder() {
  return "none";
  }

  @Override
  public java.lang.String scrollerBackgroundColor() {
  return "#007cd1";
  }

  @Override
  public java.lang.String bodyBackgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails tabStripPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String lastStopColor() {
  return "#a8d04d";
  }

  @Override
  public int iconLeftOffset() {
  return 6;
  }

  @Override
  public java.lang.String tabBodyBorder() {
  return "none";
  }

  @Override
  public int tabBarBottomHeight() {
  return 4;
  }

  @Override
  public java.lang.String hoverGradient() {
  return "#5fa7db 0%, #5fa7db 100%";
  }

  @Override
  public int tabSpacing() {
  return 1;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails paddingWithIcon() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 18;
  }

  @Override
  public int right() {
  return 18;
  }

  @Override
  public int bottom() {
  return 18;
  }

  @Override
  public int left() {
  return 18;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String tabItemBorderRight() {
  return "none";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails activeHeadingText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#027dce";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public java.lang.String tabItemBorderTop() {
  return "none";
  }

  @Override
  public java.lang.String inactiveGradient() {
  return "#48b868 0%, #48b868 100%";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails hoverHeadingText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#ffffff";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails paddingWithClosable() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 19;
  }

  @Override
  public int right() {
  return 19;
  }

  @Override
  public int bottom() {
  return 19;
  }

  @Override
  public int left() {
  return 19;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public java.lang.String inactiveLastStopColor() {
  return "#48b868";
  }

  @Override
  public java.lang.String tabStripBottomBorder() {
  return "none";
  }

  @Override
  public int iconTopOffset() {
  return 5;
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ListViewDetails listview() {
  return new edu.arizona.biosemantics.oto2.theme.client.ListViewDetails() {

  @Override
  public java.lang.String backgroundColor() {
  return "#ffffff";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ListViewDetails.ItemDetails overItem() {
  return new edu.arizona.biosemantics.oto2.theme.client.ListViewDetails.ItemDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#d6e8f6 0%, #d6e8f6 100%";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ListViewDetails.ItemDetails selectedItem() {
  return new edu.arizona.biosemantics.oto2.theme.client.ListViewDetails.ItemDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#c1ddf1 0%, #c1ddf1 100%";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails text() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "13px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "helvetica, arial, verdana, sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ListViewDetails.ItemDetails item() {
  return new edu.arizona.biosemantics.oto2.theme.client.ListViewDetails.ItemDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 6;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 6;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String style() {
  return "none";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String gradient() {
  return "#ffffff 0%, #ffffff 100%";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String lineHeight() {
  return "22px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.TipDetails errortip() {
  return new edu.arizona.biosemantics.oto2.theme.client.TipDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails margin() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String backgroundColor() {
  return "#eaf3fa";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public double opacity() {
  return 1.0;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails messagePadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 4;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#e1e1e1";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails headerText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 3;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails radiusMinusBorderWidth() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 2;
  }

  @Override
  public int right() {
  return 2;
  }

  @Override
  public int bottom() {
  return 2;
  }

  @Override
  public int left() {
  return 2;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails messageText() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#000000";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "normal";
  }

  @Override
  public java.lang.String family() {
  return "Tahoma, Arial, Verdana, sans-serif";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails headerPadding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 0;
  }

  @Override
  public int right() {
  return 0;
  }

  @Override
  public int bottom() {
  return 0;
  }

  @Override
  public int left() {
  return 0;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }
    };
  }

  @Override
  public double disabledOpacity() {
  return 0.5;
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.ButtonDetails button() {
  return new edu.arizona.biosemantics.oto2.theme.client.ButtonDetails() {

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails padding() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String mediumFontSize() {
  return "14";
  }

  @Override
  public java.lang.String overGradient() {
  return "#4792C8, #3386C2 50%, #307FB8 51%, #3386C2";
  }

  @Override
  public java.lang.String gradient() {
  return "#4B9CD7 0%, #3892D3 50%, #358AC8 51%, #3892D3";
  }

  @Override
  public java.lang.String largeFontSize() {
  return "16";
  }

  @Override
  public java.lang.String smallLineHeight() {
  return "18";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.EdgeDetails radiusMinusBorderWidth() {
  return new edu.arizona.biosemantics.oto2.theme.client.EdgeDetails() {

  @Override
  public int top() {
  return 3;
  }

  @Override
  public int right() {
  return 3;
  }

  @Override
  public int bottom() {
  return 3;
  }

  @Override
  public int left() {
  return 3;
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public java.lang.String largeLineHeight() {
  return "32";
  }

  @Override
  public java.lang.String pressedGradient() {
  return "#2A6D9E, #276796 50%, #2A6D9E 51%, #3F7BA7";
  }

  @Override
  public java.lang.String arrowColor() {
  return "#ffffff";
  }

  @Override
  public java.lang.String smallFontSize() {
  return "12";
  }

  @Override
  public java.lang.String mediumLineHeight() {
  return "24";
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.BorderDetails border() {
  return new edu.arizona.biosemantics.oto2.theme.client.BorderDetails() {

  @Override
  public int top() {
  return 1;
  }

  @Override
  public java.lang.String color() {
  return "#126DAF";
  }

  @Override
  public int right() {
  return 1;
  }

  @Override
  public int bottom() {
  return 1;
  }

  @Override
  public int left() {
  return 1;
  }

  @Override
  public java.lang.String style() {
  return "solid";
  }

  @Override
  public java.lang.String toString () {
    return top() + "px " + right() + "px " + bottom() + "px " + left() + "px";
  }
    };
  }

  @Override
  public edu.arizona.biosemantics.oto2.theme.client.FontDetails font() {
  return new edu.arizona.biosemantics.oto2.theme.client.FontDetails() {

  @Override
  public java.lang.String color() {
  return "#FFFFFF";
  }

  @Override
  public java.lang.String size() {
  return "12px";
  }

  @Override
  public java.lang.String weight() {
  return "bold";
  }

  @Override
  public java.lang.String family() {
  return "helvetica,arial,verdana,sans-serif";
  }
    };
  }

  @Override
  public int borderRadius() {
  return 4;
  }
    };
  }
public String getName() {return "theme";}

}

