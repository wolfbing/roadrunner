
	var the_cats  = new Array();

    function showLayer(the_div, the_change)
	  {
	  if(HM_IsMenu)
	    {
        div_string = "window.document.all." + the_div + ".style";
	    real_div = eval(div_string);

	    if(real_div.visibility == the_change)
		  return;

	    real_div.visibility = the_change;

		if(the_change == "visible")
		  AddLayer(the_div);
		else
		  DelLayer(the_div);
	    }
      }

	function AddLayer(the_div)
	  {
	  var lp = 0;

      while(the_cats[lp] != "")
        lp++;

      the_cats[lp] = the_div;
	  }

	function DelLayer(the_div)
	  {
	  var lp = 0;

      while(the_cats[lp] != the_div)
        lp++;

      the_cats[lp] = "";
	  }

	function closeAll()
	  {
      for(var lp = 0; lp < 32; lp++)
        if(the_cats[lp] != "")
          showLayer(the_cats[lp], "hidden", 1);
	  }

	function WriteLayerOn(name, yPos, xPos, zInx, vis)
	  {
      document.write("<div id='" + name + "' style='position:absolute; top:" + yPos + "; left:" + xPos + "; z-index:" + zInx + "; visibility:" + vis + ";'> ");
	  }

	function WriteLayerOff()
	  {
      document.write("</div>");
	  }

	for(var lp = 0; lp < 32; lp++)
	  {
	  the_cats[lp] = "";
	  }

