(function($,W,D){

    $.times = new Object();
    $.series = new Object();

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

    var getSerie = function(series, serieName){
        var ans = undefined;
        $.each(series, function(idx, serie){
            if(serie.name===serieName)
            {
                ans = serie;
                return false;
            }
        });
        if(ans === undefined)
            console.log('serieName: '+serieName+' is undefined for vmid: '+vmid)
        return ans;
    }

    var createChartForJvm = function(vmid, vmname)
    {
        $.times[vmid] = new Object();
        $.series[vmid] = new Array();
        $.getJSON("/os-monitoring-server/api/processes/"+vmid+"/metrics/?since=0", function(results) {
            if (results)
            {

                $.each(results.series, function(idx, serie){
                    var samples = new Array();
                    $.each(serie.samples, function(idx, sample){
                        $.times[vmid][serie.legend] = sample.date // store the latest time value
                        samples.push([sample.date, sample.data]);
                    });
                    $.series[vmid].push( { name: serie.legend, data: samples } );
                });

                // Create the chart
                $('#chart-'+vmid+' .chart').highcharts('StockChart', {
                    chart : {
                        events : {
                            load : function() {
                                var that = this;
                                setInterval(function() {
                                    $.getJSON("/os-monitoring-server/api/processes/"+vmid+"/metrics/?since="+(new Date().getTime()-2000), function(results) {
                                        if (results)
                                        {
                                            var series = new Array();
                                            $.each(results.series, function(idx, serie){
                                                $.each(serie.samples, function(idx, sample){
                                                    if($.times[vmid][serie.legend]<sample.date) // we only add new point
                                                    {
                                                        $.times[vmid][serie.legend] = sample.date // store the latest time value
                                                        getSerie(that.series, serie.legend).addPoint([sample.date, sample.data], false, true);
                                                    }
                                                });
                                            });
                                            that.redraw();
                                        }
                                    });
                                }, 2000);
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
                        enabled: true
                    },

                    series : $.series[vmid]
                });
            }
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