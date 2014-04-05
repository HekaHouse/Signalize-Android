var yVal = 0.0;
var xVal =0.0;
var dps = [];
var neg_dps = [];

var dps_sum = 0;
var neg_dps_sum = 0;

var tier_one = 0;
var tier_two = 0;
var tier_three = 0;
var last_update=-1;
var index = 0;
var active = true;
// window['Signalize']=[];
// window['Signalize']['incomingTier']=function(index) {var rand = Math.round(Math.random() * (3 - 1)) + 1; return rand;}
// window['Signalize']['incomingSentiment'] = function(index) { return Math.random(); }

function collectTier(sma,chart)
{
    try
      {
        var incoming=window.Signalize.incomingTier(index);
        if (incoming > -1) {
            if (incoming == 1) {
                tier_one++;
            } else if (incoming == 2) {
                tier_two++;
            } else {
                tier_three++;
            }            
            index++;
        }
        updateChart(sma,chart,"tier_guage");
      }
    catch(err)
      {

      }


}
function collectSentiment(sma,chart,charttype)
{
  var incoming;
  try
  {
    incoming=window.Signalize.incomingSentiment(index,charttype);
    if (incoming > -1) {
        yVal=100*incoming;        
        index++;
    }
    updateChart(sma,chart,charttype);
  }
  catch(err)
  {

  }
}
function simple_moving_averager(period) 
{
    var nums = [];
    return function(num) {
        nums.push(num);
        if (nums.length > period)
            nums.splice(0,1);  // remove the first element of the array
        var sum = 0;
        for (var i in nums)
            sum += nums[i];
        var n = period;
        if (nums.length < period)
            n = nums.length;
        return(sum/n);
    }
}

function updateChart(sma,chart,charttype) 
{
      if (last_update < index) {
        last_update = index;
        xVal++;           
        if (sma) {                    
          pos = sma(yVal);
          neg = sma(100-yVal);
          dps.push({x: xVal,y: pos});
          neg_dps.push({x: xVal,y: neg});
        }
        
        if (yVal > 50) 
        {
          dps_sum++;
        } else {
          neg_dps_sum++;
        }
              
        if (dps.length >  10 )
        {
            dps.shift();        
            neg_dps.shift();
        }
      }

      if (charttype=="sentiment_gauge") {
        chart = buildSentimentGauge();
      } else if (charttype=="tier_gauge") {
        chart = buildTierGauge();
      }
      chart.render();
}
function buildTierGauge() {
  return new CanvasJS.Chart("chartContainer", {
                  animationEnabled: false,
                  data: [ { indexLabelFontColor: "white", indexLabelPlacement: "inside", type: "doughnut", dataPoints: [ {  y: tier_three, indexLabel: "3: "+tier_three }, {  y: tier_two, indexLabel: "2: "+tier_two }, {  y: tier_one, indexLabel: "1: "+tier_one } ] } ] });
}
function buildSentimentGauge() {
  return new CanvasJS.Chart("chartContainer", { 
              animationEnabled: false, 
              data: [ { indexLabelFontColor: "white", indexLabelPlacement: "inside", type: "doughnut", dataPoints: [ {  y: dps_sum, indexLabel: "+("+dps_sum+")" }, {  y: neg_dps_sum, indexLabel: "-("+neg_dps_sum+")" } ] } ] });
}
function buildSentimentTrack() {
  return new CanvasJS.Chart("chartContainer",{             
              axisY: {                        

                        tickColor: "white" ,
                        gridColor: "white" ,
                        lineColor: "white"
              },
                  axisX: {                                                          
                        valueFormatString: " ",
                        tickColor: "white" ,
                        gridColor: "white" ,
                        lineColor: "white"
                  },
              data: [{
                        type: "stackedArea100",
                        indexLabelLineColor: "white",                        
                        dataPoints : dps
                  },{
                        type: "stackedArea100",
                        dataPoints : neg_dps
                  }]            
            });
}