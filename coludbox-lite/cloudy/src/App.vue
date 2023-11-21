<template>


  <HelloWorld/>
</template>

<script>
//import HelloWorld from './components/HelloWorld.vue'





import HelloWorld from "@/components/HelloWorld.vue";

export default {
  name: 'App',
  components: {
    HelloWorld,



  }
};
</script>


<style>


@import "../../assets/css/backend-plugin.min.css ";
@import "../../assets/css/backend.css?v=1.0.0 ";
@import "../../assets/vendor/@fortawesome/fontawesome-free/css/all.min.css ";
@import "../../assets/vendor/line-awesome/dist/line-awesome/css/line-awesome.min.css ";
@import "../../assets/vendor/remixicon/fonts/remixicon.css ";
@import "../../assets/vendor/doc-viewer/include/pdf/pdf.viewer.css ";
@import "../../assets/vendor/doc-viewer/include/PPTXjs/css/pptxjs.css ";
@import "../../assets/vendor/doc-viewer/include/PPTXjs/css/nv.d3.min.css ";
@import "../../assets/vendor/doc-viewer/include/SheetJS/handsontable.full.min.css ";
@import "../../assets/vendor/doc-viewer/include/verySimpleImageViewer/css/jquery.verySimpleImageViewer.css ";
@import "../../assets/vendor/doc-viewer/include/officeToHtml/officeToHtml.css ";
</style>
