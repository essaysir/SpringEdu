/*
 Highcharts JS v11.0.1 (2023-05-08)
 Organization chart series type

 (c) 2019-2021 Torstein Honsi

 License: www.highcharts.com/license
*/
'use strict';(function(d){"object"===typeof module&&module.exports?(d["default"]=d,module.exports=d):"function"===typeof define&&define.amd?define("highcharts/modules/organization",["highcharts","highcharts/modules/sankey"],function(n){d(n);d.Highcharts=n;return d}):d("undefined"!==typeof Highcharts?Highcharts:void 0)})(function(d){function n(d,a,b,f){d.hasOwnProperty(a)||(d[a]=f.apply(null,b),"function"===typeof CustomEvent&&window.dispatchEvent(new CustomEvent("HighchartsModuleLoaded",{detail:{path:a,
module:d[a]}})))}d=d?d._modules:{};n(d,"Series/Organization/OrganizationPoint.js",[d["Core/Series/SeriesRegistry.js"],d["Core/Utilities.js"]],function(d,a){function b(c){let e=c.linksFrom.length;c.linksFrom.forEach(c=>{c.id===c.toNode.linksTo[0].id?e+=b(c.toNode):e--});return e}const {seriesTypes:{sankey:{prototype:{pointClass:f}}}}=d,{defined:g,find:c,pick:k}=a;class h extends f{constructor(){super(...arguments);this.toNode=this.series=this.options=this.linksTo=this.linksFrom=this.fromNode=void 0}init(){f.prototype.init.apply(this,
arguments);this.isNode||(this.dataLabelOnNull=!0,this.formatPrefix="link");return this}getSum(){return 1}setNodeColumn(){super.setNodeColumn();const e=this,a=e.getFromNode().fromNode;if(!g(e.options.column)&&0!==e.linksTo.length&&a&&"hanging"===a.options.layout){e.options.layout=k(e.options.layout,"hanging");e.hangsFrom=a;let f=-1;c(a.linksFrom,function(b,c){(b=b.toNode===e)&&(f=c);return b});for(let c=0;c<a.linksFrom.length;c++){let l=a.linksFrom[c];l.toNode.id===e.id?c=a.linksFrom.length:f+=b(l.toNode)}e.column=
(e.column||0)+f}}}return h});n(d,"Series/Organization/OrganizationSeriesDefaults.js",[],function(){"";return{borderColor:"#666666",borderRadius:3,link:{color:"#666666",lineWidth:1,radius:10,type:"default"},borderWidth:1,dataLabels:{nodeFormatter:function(){function d(c){return Object.keys(c).reduce(function(b,l){return b+l+":"+c[l]+";"},'style="')+'"'}var a={width:"100%",height:"100%",display:"flex","flex-direction":"row","align-items":"center","justify-content":"center"};const b={"max-height":"100%",
"border-radius":"50%"},f={width:"100%",padding:0,"text-align":"center","white-space":"normal"},g={margin:0},c={margin:0},k={opacity:.75,margin:"5px"},{description:h,image:e,title:q}=this.point;e&&(b["max-width"]="30%",f.width="70%");this.series.chart.renderer.forExport&&(a.display="block",f.position="absolute",f.left=e?"30%":0,f.top=0);a="<div "+d(a)+">";e&&(a+='<img src="'+e+'" '+d(b)+">");a+="<div "+d(f)+">";this.point.name&&(a+="<h4 "+d(g)+">"+this.point.name+"</h4>");q&&(a+="<p "+d(c)+">"+(q||
"")+"</p>");h&&(a+="<p "+d(k)+">"+h+"</p>");return a+"</div></div>"},style:{fontWeight:"normal",fontSize:"0.9em"},useHTML:!0,linkTextPath:{attributes:{startOffset:"95%",textAnchor:"end"}}},hangingIndent:20,hangingIndentTranslation:"inherit",minNodeLength:10,nodeWidth:50,tooltip:{nodeFormat:"{point.name}<br>{point.title}<br>{point.description}"}}});n(d,"Series/PathUtilities.js",[],function(){function d(a,b){const d=[];for(let f=0;f<a.length;f++){const h=a[f][1],e=a[f][2];if("number"===typeof h&&"number"===
typeof e)if(0===f)d.push(["M",h,e]);else if(f===a.length-1)d.push(["L",h,e]);else if(b){var g=a[f-1],c=a[f+1];if(g&&c){const a=g[1];g=g[2];const f=c[1];c=c[2];if("number"===typeof a&&"number"===typeof f&&"number"===typeof g&&"number"===typeof c&&a!==f&&g!==c){const k=a<f?1:-1,l=g<c?1:-1;d.push(["L",h-k*Math.min(Math.abs(h-a),b),e-l*Math.min(Math.abs(e-g),b)],["C",h,e,h,e,h+k*Math.min(Math.abs(h-f),b),e+l*Math.min(Math.abs(e-c),b)])}}}else d.push(["L",h,e])}return d}return{applyRadius:d,getLinkPath:{"default":function(a){const {x1:b,
y1:f,x2:g,y2:c,width:k=0,inverted:h=!1,radius:e,parentVisible:q}=a;a=[["M",b,f],["L",b,f],["C",b,f,b,c,b,c],["L",b,c],["C",b,f,b,c,b,c],["L",b,c]];return q?d([["M",b,f],["L",b+k*(h?-.5:.5),f],["L",b+k*(h?-.5:.5),c],["L",g,c]],e):a},straight:function(a){const {x1:b,y1:d,x2:g,y2:c,width:k=0,inverted:h=!1,parentVisible:e}=a;return e?[["M",b,d],["L",b+k*(h?-1:1),c],["L",g,c]]:[["M",b,d],["L",b,c],["L",b,c]]},curved:function(d){const {x1:b,y1:a,x2:g,y2:c,offset:k=0,width:h=0,inverted:e=!1,parentVisible:q}=
d;return q?[["M",b,a],["C",b+k,a,b-k+h*(e?-1:1),c,b+h*(e?-1:1),c],["L",g,c]]:[["M",b,a],["C",b,a,b,c,b,c],["L",g,c]]}}}});n(d,"Series/Organization/OrganizationSeries.js",[d["Series/Organization/OrganizationPoint.js"],d["Series/Organization/OrganizationSeriesDefaults.js"],d["Core/Series/SeriesRegistry.js"],d["Series/PathUtilities.js"],d["Core/Utilities.js"]],function(d,a,b,f,g){const {seriesTypes:{sankey:c}}=b,{css:k,extend:h,isNumber:e,merge:q,pick:n}=g;class t extends c{constructor(){super(...arguments);
this.points=this.options=this.data=void 0}alignDataLabel(c,d,b){var a=c.shapeArgs;if(b.useHTML&&a){let c=a.width||0,b=a.height||0,l=this.options.borderWidth+2*this.options.dataLabels.padding;this.chart.inverted&&(c=b,b=a.width||0);b-=l;c-=l;if(a=d.text)k(a.element.parentNode,{width:c+"px",height:b+"px"}),k(a.element,{left:0,top:0,width:"100%",height:"100%",overflow:"hidden"});d.getBBox=()=>({width:c,height:b,x:0,y:0});d.width=c;d.height=b}super.alignDataLabel.apply(this,arguments)}createNode(c){c=
super.createNode.call(this,c);c.getSum=()=>1;return c}pointAttribs(b,d){const a=c.prototype.pointAttribs.call(this,b,d);var f=this.mapOptionsToLevel[(b.isNode?b.level:b.fromNode.level)||0]||{};const m=b.options,l=f.states&&f.states[d]||{};d=n(l.borderRadius,m.borderRadius,f.borderRadius,this.options.borderRadius);const p=n(l.linkColor,m.linkColor,f.linkColor,this.options.linkColor,l.link&&l.link.color,m.link&&m.link.color,f.link&&f.link.color,this.options.link&&this.options.link.color),h=n(l.linkLineWidth,
m.linkLineWidth,f.linkLineWidth,this.options.linkLineWidth,l.link&&l.link.lineWidth,m.link&&m.link.lineWidth,f.link&&f.link.lineWidth,this.options.link&&this.options.link.lineWidth);f=n(l.linkOpacity,m.linkOpacity,f.linkOpacity,this.options.linkOpacity,l.link&&l.link.linkOpacity,m.link&&m.link.linkOpacity,f.link&&f.link.linkOpacity,this.options.link&&this.options.link.linkOpacity);b.isNode?e(d)&&(a.r=d):(a.stroke=p,a["stroke-width"]=h,a.opacity=f,delete a.fill);return a}translateLink(c){var b=c.fromNode;
let a=c.toNode,d=n(this.options.linkLineWidth,this.options.link.lineWidth),e=Math.round(d)%2/2,l=n(this.options.link.offset,.5),h=n(c.options.link&&c.options.link.type,this.options.link.type);if(b.shapeArgs&&a.shapeArgs){let m=Math.floor((b.shapeArgs.x||0)+(b.shapeArgs.width||0))+e,k=Math.floor((b.shapeArgs.y||0)+(b.shapeArgs.height||0)/2)+e,p=Math.floor(a.shapeArgs.x||0)+e,r=Math.floor((a.shapeArgs.y||0)+(a.shapeArgs.height||0)/2)+e;let q=this.options.hangingIndent;var g=a.options.offset;let u=/%$/.test(g)&&
parseInt(g,10),t=this.chart.inverted;t&&(m-=b.shapeArgs.width||0,p+=a.shapeArgs.width||0);g=this.colDistance?Math.floor(p+(t?1:-1)*(this.colDistance-this.nodeWidth)/2)+e:Math.floor((p+m)/2)+e;u&&(50<=u||-50>=u)&&(g=p=Math.floor(p+(t?-.5:.5)*(a.shapeArgs.width||0))+e,r=a.shapeArgs.y||0,0<u&&(r+=a.shapeArgs.height||0));a.hangsFrom===b&&(this.chart.inverted?(k=Math.floor((b.shapeArgs.y||0)+(b.shapeArgs.height||0)-q/2)+e,r=(a.shapeArgs.y||0)+(a.shapeArgs.height||0)):k=Math.floor((b.shapeArgs.y||0)+q/
2)+e,g=p=Math.floor((a.shapeArgs.x||0)+(a.shapeArgs.width||0)/2)+e);c.plotX=g;c.plotY=(k+r)/2;c.shapeType="path";"straight"===h?c.shapeArgs={d:[["M",m,k],["L",p,r]]}:"curved"===h?(b=Math.abs(p-m)*l*(t?-1:1),c.shapeArgs={d:[["M",m,k],["C",m+b,k,p-b,r,p,r]]}):c.shapeArgs={d:f.applyRadius([["M",m,k],["L",g,k],["L",g,r],["L",p,r]],n(this.options.linkRadius,this.options.link.radius))};c.dlBox={x:(m+p)/2,y:(k+r)/2,height:d,width:0}}}translateNode(b,a){c.prototype.translateNode.call(this,b,a);a=b.hangsFrom;
let d=this.options.hangingIndent||0,f=this.chart.inverted?-1:1,e=b.shapeArgs,g=this.options.hangingIndentTranslation,h=this.options.minNodeLength||10;if(a)if("cumulative"===g)for(e.height-=d,e.y-=f*d;a;)e.y+=f*d,a=a.hangsFrom;else if("shrink"===g)for(;a&&e.height>d+h;)e.height-=d,a=a.hangsFrom;else e.height-=d,this.chart.inverted||(e.y+=d);b.nodeHeight=this.chart.inverted?e.width:e.height}drawDataLabels(){const a=this.options.dataLabels;if(a.linkTextPath&&a.linkTextPath.enabled)for(const a of this.points)a.options.dataLabels=
q(a.options.dataLabels,{useHTML:!1});super.drawDataLabels()}}t.defaultOptions=q(c.defaultOptions,a);h(t.prototype,{pointClass:d});b.registerSeriesType("organization",t);"";return t});n(d,"masters/modules/organization.src.js",[],function(){})});
//# sourceMappingURL=organization.js.map