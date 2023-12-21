<template>
  <RouterView/>

  <!--
  <div id="show">
    <button @click="openModal" class="btn btn-primary">
      Open PDF
    </button>


    <transition  name="fade">
      <div v-if="modalShow" class="popup-modal">
        <div class="popup-modal-content">
          <button class="popup-close-btn" @click="closeModal">×</button>
          <div class="modal-header">
            <h5 class="modal-title">PDF Viewer</h5>
          </div>
          <div class="modal-body">

            <PdfViewer :path="path" :fileName="name" style="width: 100%; height: 420px;" />
          </div>
        </div>
      </div>
    </transition>
  </div>-->
</template>

<script>

export default {
  name: 'App',
  components: {
      },
  data() {
    return {

    };
  },
  methods: {

  },
};
</script>

<style scoped>
/* Modal stillendirmeleri */



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
