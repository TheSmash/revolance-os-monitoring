(function($,W,D){

    $.fn.chartList = function(options) {

        // Options par defaut
        var defaults = { };

        var options = $.extend(defaults, options);

        this.each(function(){

            var obj = $(this);

            // Empêcher la sélection des éléments à la sourirs (meilleure gestion du drag & drop)
            var _preventDefault = function(evt) { evt.preventDefault(); };
            $("li").bind("dragstart", _preventDefault).bind("selectstart", _preventDefault);

            // Initialisation du composant "sortable"
            $(obj).sortable({
                axis: "y", // Le sortable ne s'applique que sur l'axe vertical
                containment: "#chart-list", // Le drag ne peut sortir de l'élément qui contient la liste
                handle: "li", // Le drag ne peut se faire que sur l'élément .item (le texte)
                distance: 10, // Le drag ne commence qu'à partir de 10px de distance de l'élément
                // Evenement appelé lorsque l'élément est relaché
                stop: function(event, ui){
                    // Pour chaque item de liste
                    // $(obj).find("li").each(function(){
                        // On actualise sa position
                        // index = parseInt($(this).index()+1);
                        // On la met à jour dans la page
                        // $(this).find(".count").text(index);
                    // });
                }
            });

        });
        // On continue le chainage JQuery
        return this;
    };

    var createChartForJvm = function(vmid, vmname)
    {
        $.series = new Array();
        $.times = new Object();
        $.getJSON("/jvm-monitoring-server/api/vms/"+vmid+"/metrics", function(metrics)
        {
            $.metrics = metrics.labels;
            $.each($.metrics, function(idx, metric)
            {
                $.getJSON("/jvm-monitoring-server/api/vms/"+vmid+"/metrics/"+metric+"/?since=0", function(results) {
                    if (results)
                    {
                        var samples = $.map(results.samples, function(sample){
                            var array = new Array();
                            array.push(sample.date)
                            array.push(sample.data)

                            $.times[metric] = sample.date // store the latest time value
                            return [array];
                        });
                        serie = { name: metric, data: samples };
                        $.series.push( serie );
                        if($.series.length===$.metrics.length)
                        {
                            // Create the chart
                            $('#chart-'+vmid+' .chart').highcharts('StockChart', {
                                chart : {
                                    events : {
                                        load : function() {
                                            var that = this;
                                            setInterval(function() {
                                                $.each($.series, function(idx, serie){
                                                    $.getJSON("/jvm-monitoring-server/api/vms/"+vmid+"/metrics/"+metric+"/?since="+($.times[serie.name]-1000), function(results){
                                                        if(results && results.samples)
                                                        {
                                                            $.each(results.samples, function(idx, sample){
                                                                var array = new Array();
                                                                array.push(sample.date)
                                                                array.push(sample.data)

                                                                that.series[idx].addPoint([array], true, true);
                                                            });
                                                            if(results.samples.length>0)
                                                                $.times[metric] = results.samples[results.samples.length-1].date;
                                                        }
                                                    });
                                                });
                                            }, 2000);
                                            /*
                                            // set up the updating of the chart each second
                                            var series = this.series[0];
                                            setInterval(function() {
                                                var x = (new Date()).getTime(), // current time
                                                y = Math.round(Math.random() * 100);
                                                series.addPoint([x, y], true, true);
                                            }, 1000);
                                            */
                                        }
                                    }
                                },

                                rangeSelector: {
                                    buttons: [{
                                        count: 1,
                                        type: 'minute',
                                        text: '1M'
                                    }, {
                                        count: 5,
                                        type: 'minute',
                                        text: '5M'
                                    }, {
                                        type: 'all',
                                        text: 'All'
                                    }],
                                    inputEnabled: false,
                                    selected: 0
                                },

                                title : {
                                    text : vmname
                                },

                                exporting: {
                                    enabled: false
                                },

                                series : $.series
                            });
                        }
                    }
                });
            });
        });
    }

    $.addChartForJvm = function(jvmId, jvmName)
    {;
        $("#chart-list").append('<li id="chart-'+jvmId+'"><div class="chart"></div></li>');
        createChartForJvm(jvmId, jvmName);
    }

    $.isChartForJvmDisplayed = function(vmid)
    {
        if($("#chart-list li").length === 0)
        {
            return false;
        }
        else
        {
            return $("#chart-list li").attr("id").indexOf("chart-"+vmid)>=0;
        }
    }

    $.delChartForJvm = function(jvmId)
    {
        $("#chart-"+jvmId).remove();
    }

     $(document).ready(function( ){
            Highcharts.setOptions({
                global : {
                    useUTC : false
                }
            });
     });

})(jQuery, window, document);