<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:transform version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" encoding="iso-8859-1" />
  <!-- without a base element images could not be displayed offline from a local mirror -->
  <!-- however, they usually are not  displayed in distributions different from the one -->
  <!-- which generated the data set because of dependance on an absolute url            -->
  <xsl:variable name="baseref"><xsl:value-of select="//instance[1]/@source"/></xsl:variable>
  <xsl:template match="/dataset">
    <html>
    <head>
      <style type="text/css">
         table       { border-collapse: separate;
                       empty-cells: show;
                     }
       div.subtree {   overflow: scroll;
                       font-size: 9px;
                       border-color: gray; 
                       border:solid; 
                       border-width: 5px;
                       border-style: outset;                       
                       width: 320px;
                       max-height: 256px;
                     }
       div.text     {  overflow: visible;
                        width: 80px;
                        height: auto;
                        font-size: 9px;
                     }
       div.image    {   overflow: visible;
                        width: 150px;                        
                    }                     
       div.link     {   width: 30px;
                        font-size: 9px;
                    }
       div.null     {   font-style: italic;
                        width: 80px;                      
                        font-size: 9px;
                    }                    
       div.null_image     
                    {   font-style: italic;
                        width: 150px;                      
                        font-size: 9px;
                    }                    
       div.null_link
                    {   font-style: italic;
                        width: 30px;                      
                        font-size: 9px;
                    }                    
       div.null_subtree
                    {  font-style: italic;
                       overflow: scroll;
                       border-color: gray; 
                       border:solid; 
                       border-width: 5px;
                       border-style: outset;                       
                       width: 320px;
                       max-height: 256px;
                    }                    
      </style>
      <base href="{$baseref}"/> <!-- HACK TO DISPLAY IMAGES FROM LOCAL MIRRORS -->
      <title>The RoadRunner Project</title>
    </head>
    <body>
    <h1>Extracted DataSet</h1>
    <table border="5">
      <tr bgcolor="yellow"><th><xsl:value-of select="@wrappedby"/></th></tr>
      <tr><td><xsl:apply-templates/></td></tr>
    </table>
    </body>
    </html>
  </xsl:template>
   
  <xsl:template match="instance">
  <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
  <xsl:variable name="src"><xsl:value-of select="@source"/></xsl:variable>
    <h3>
      <a name="{$name}"/><a href="{$src}" target="_blank"><xsl:value-of select="@source"/></a>
    </h3>
    <table border="2" valign="top">
       <xsl:for-each select="and">
          <tr>
            <xsl:for-each select="attribute|plus|hook|subtree|null_subtree">
               <th align="center" valign="top">
                 <xsl:value-of select="@label"/>
               </th>
            </xsl:for-each>
          </tr>
       </xsl:for-each>
       <xsl:apply-templates select="and"/>
    </table>
  </xsl:template>

  <xsl:template match="and">
    <tr>
       <xsl:apply-templates select="attribute|plus|hook|subtree|null_subtree"/>
    </tr>
  </xsl:template>

  <xsl:template match="plus">
    <td align="center" valign="top">
      <table border="1" width="100%">
         <xsl:for-each select="and[1]">
            <tr>
              <xsl:for-each select="attribute|plus|hook|subtree|null_subtree">
                 <th align="center" valign="top">
                   <xsl:value-of select="@label"/>
                 </th>
              </xsl:for-each>
            </tr>
         </xsl:for-each>
         <xsl:apply-templates select="and"/>
      </table>
    </td>
  </xsl:template>

  <xsl:template match="hook">
    <td align="center" valign="top">
      <table border="0" width="100%">
        <xsl:for-each select="and[1]">
          <tr>
            <xsl:for-each select="attribute|plus|hook">
               <th align="center" valign="top">
                 <xsl:value-of select="@label"/>
               </th>
            </xsl:for-each>
          </tr>
        </xsl:for-each>
        <xsl:apply-templates select="and"/>
      </table>
    </td>
  </xsl:template>

  <xsl:template match="subtree">
    <td align="center" valign="top">
      <div class="subtree">
          <xsl:copy-of select="@*|node()"/>
      </div>
    </td>
  </xsl:template>

  <xsl:template match="null_subtree">
    <td align="center" valign="top">
      <div class="null_subtree">null</div>
    </td>
  </xsl:template>

  
  <xsl:template match="attribute">
    <td align="center" valign="top">
      <div class="attribute">
        <xsl:apply-templates select="text()|image|link|null|null_link|null_image"/>
      </div>
    </td>
  </xsl:template>

  <xsl:template match="text()">
    <div class="text"><xsl:value-of select="."/></div>
  </xsl:template>

  <xsl:template match="image">
      <xsl:variable name="source"><xsl:value-of select="@source"/></xsl:variable>
      <div class="image"><img src="{$source}"/></div>
  </xsl:template>

  <xsl:template match="link">
      <xsl:variable name="target"><xsl:value-of select="@target"/></xsl:variable>
      <div class="link"><a href="{$target}" target="_blank">link</a></div>
  </xsl:template>

  <xsl:template match="null">
      <div class="null">null</div>
  </xsl:template>
  <xsl:template match="null_link">
      <div class="null_link">null</div>
  </xsl:template>
  <xsl:template match="null_image">
      <div class="null_image">null</div>
  </xsl:template>
 
</xsl:transform>
