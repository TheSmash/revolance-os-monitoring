<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>

    <head>

        <title>Java Virtual Machine Monitoring | Revolance</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- JQuery -->
        <script src="${pageContext.request.contextPath}/libs/jquery/js/jquery.min.js"></script>

        <!-- JQuery UI -->
        <script src="${pageContext.request.contextPath}/libs/jquery/js/jquery-ui-1.10.3.custom.min.js"></script>
        <script src="${pageContext.request.contextPath}/libs/jquery/css/jquery-ui-1.10.3.custom.min.css"></script>

        <!-- Twitter Bootstrap-->
        <link href="${pageContext.request.contextPath}/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
        <script src="${pageContext.request.contextPath}/libs/bootstrap/js/bootstrap.min.js"></script>

        <!-- D3 -->
        <script src="${pageContext.request.contextPath}/libs/d3/js/d3.js" charset="utf-8"></script>

        <!-- Cubism -->
        <script src="${pageContext.request.contextPath}/libs/cubism/js/cubism.js" charset="utf-8"></script>
        <link href="${pageContext.request.contextPath}/libs/cubism/css/cubism.css" rel="stylesheet"/>

        <!-- D3 -->
        <script src="${pageContext.request.contextPath}/libs/highlight/js/highlight.js" charset="utf-8"></script>

        <!-- Revolance JSs -->
        <script src="${pageContext.request.contextPath}/js/jvm-list.js"></script>
        <script src="${pageContext.request.contextPath}/js/chart-list.js"></script>

        <!-- Revolance Css -->
        <link href="${pageContext.request.contextPath}/css/viewer.css" rel="stylesheet"/>

        <style>
            .error {
                color: #ff0000;
            }

            .errorblock {
                color: #000;
                background-color: #ffEEEE;
                border: 3px solid #ff0000;
                padding: 8px;
                margin: 16px;
            }
        </style>

    </head>

    <body>