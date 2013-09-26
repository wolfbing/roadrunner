<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:transform version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" encoding="iso-8859-1" />
  <xsl:template match="/index">
    <html>
    <head>
      <style type="text/css">
     .header { font-size:14px; background-color:#111188; color:#FFFFFF; }
        .sep { background-color:#FFFFFF; color:#FFFFFF; height: 10px; }
        body { font-size:14px; background-color:#FFFFFF; color:#111188; }
     caption { font-size:24px; }
.wrapper_data   
             { background-color:#FFFFFF; 
               color:#000000; 
             }
     .data   { background-color:#CCCCCC; }
      </style>
    </head>
    <body>
    <small>
    
    <table  border="0">
        <caption>Inferred Wrappers</caption>
        <thead align="center" class="header"><tr><th rowspan="2">Wrapper</th><th rowspan="2" align="right">CR=   </th><th><u>sdl+idl</u></th></tr>
                              <tr><th> dl </th></tr>
        </thead>
        <tbody align="center"><xsl:apply-templates select="wrapper"/></tbody>
    </table>
    </small>
    </body>
    </html>
  </xsl:template>

  <xsl:template match="wrapper">
      <xsl:variable name="source"><xsl:value-of select="@source"/></xsl:variable>
      <tr><td colspan="3" class="sep"></td></tr>
      <tr class="wrapper_data"><td rowspan="2"><i><a href="{$source}" target="_blank"><xsl:value-of select="@name"/></a></i></td><td rowspan="2"><xsl:value-of select="@compress_ratio"/>=</td>
          <td><u><xsl:value-of select="@schema_dl"/>+<xsl:value-of select="@instances_dl"/></u></td></tr>
      <tr class="wrapper_data"><td> <xsl:value-of select="@samples_dl"/></td></tr>
      <tr class="header"><th>Sample</th><th>DL</th><th>IDL</th></tr>
      <xsl:apply-templates select="instance"/>
      <tr><td colspan="3" class="sep"></td></tr>
  </xsl:template>
    
  <xsl:template match="instance">
      <xsl:variable name="name"><xsl:value-of select="@name"/></xsl:variable>
      <xsl:variable name="dataset"><xsl:value-of select="../@dataset"/></xsl:variable>
      <tr class="data"><td><a href="{$dataset}#{$name}" target="dataset"><xsl:value-of select="@name"/></a></td><td><xsl:value-of select="@sample_dl"/></td><td><xsl:value-of select="@instance_dl"/></td></tr>
  </xsl:template>

  
</xsl:transform>
