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
                containment: "#chartList", // Le drag ne peut sortir de l'élément qui contient la liste
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

    var jvmMetric = function(vmid, metric) {
        return context.metric(function(start, stop, step, callback) {
            var time = new Date().getTime()
            $.getJSON("/jvm-monitoring-server/api/vms/"+vmid+"/metrics/"+metric+"/?since"+time, function(results) {
                    //callback(null, [100]);
                    if (!results)
                    {
                        return callback(new Error("unable to load data"));
                    }
                    else
                    {
                        callback(null, results.datas);
                    }
            });
        }, vmid+'-'+metric);
    }

    $.addChartForJvm = function(jvmId, jvmName)
    {;
        $("#chart-list").append('<li id="chart-'+jvmId+'"><div class="chart"></div></li>');
        d3.select("#chart-"+jvmId).call(function(div) {

            div.datum(jvmMetric(jvmId, "EU"));

            div.append("div")
                .attr("class", "horizon")
                .call(context.horizon().height(110).title(jvmName))
        });
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

        d3.select("#chart-"+jvmId+" .horizon")
            .call(horizon.remove)
        $("#chart-"+jvmId).remove();
    }

})(jQuery, window, document);