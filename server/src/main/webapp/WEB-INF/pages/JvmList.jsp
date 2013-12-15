<%@ include file="Header.jsp" %>

    <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
            <div class="container">
                <a class="brand" href="#">RevoLance Java Virtual Machine Monitoring</a>
                <div class="nav-collapse collapse">
                    <ul class="nav pull-right">
                        <li onclick="$.updateJvmList()"><a data-toggle="modal" href="#myModal"><span class="glyphicon glyphicon-list"></span>jvms</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>


    <div class="container" id="content">
        <ul id="chart-list"></ul>
    </div>

    <script>


    var context = cubism.context()
        .serverDelay(1e3)
        .clientDelay(1e3)
        .step(2e3)
        .size(1170);

    d3.select("#chart-list").call(function(div) {
        div.append("div")
            .attr("class", "rule")
            .call(context.rule());

         div.append("div")
                .attr("class", "axis")
                .call(context.axis().orient("top"));
    });

    </script>
    <script>

    // On mousemove, reposition the chart values to match the rule.
    context.on("focus", function(i) {
      d3.selectAll(".value").style("right", i == null ? null : context.size() - i + "px");
    });

    </script>


    <script src="${pageContext.request.contextPath}/js/jvm-list.js"></script>
    <!-- Modal -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title">Please, select the jvm you want to monitor:</h4>
        </div>
        <div class="modal-body">

            <table id="jvm-list" class="table table-hover">
                <thead>
                    <tr>
                        <th>&nbsp;&nbsp;</th>
                        <th>id</th>
                        <th>name</th>
                        <th>options</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>

        </div>
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->

<%@ include file="Footer.jsp" %>