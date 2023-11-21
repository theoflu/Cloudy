<script>
import UploadService from "@/assets/services/UploadFilesService";

export default {
  name: 'UploadFiles',
data() {
  return {
    selectedFiles: undefined,
    progressInfos: [],
    message: "",
    fileInfos: [],
  };

},
methods: {
  selectFile() {
    this.progressInfos = [];
    this.selectedFiles = event.target.files;
  },
  uploadFiles() {
    this.message = "";

    for (let i = 0; i < this.selectedFiles.length; i++) {
      this.upload(i, this.selectedFiles[i]);
      console.log(this.selectedFiles[i]);
    }
  },
  upload(idx, file) {
    this.progressInfos[idx] = { percentage: 0, fileName: file.name };

    UploadService.upload(file, (event) => {
      this.progressInfos[idx].percentage = Math.round(100 * event.loaded / event.total);

    })
        .then((response) => {


          this.message =  response.data.processes;
          alert(this.message)
          location.reload();

        })
        .then((files) => {

          this.fileInfos = files.data;
        })
        .catch(() => {
          this.progressInfos[idx].percentage = 0;
          this.message = "Could not upload the file:" + file.name;

        });
  },
  mounted() {
    UploadService.getFiles().then((response) => {
      this.fileInfos = response.data;


    });
  }
}
}
</script>

<template>
  <div class="center">
    <label class=" ">
      <input class="" type="file" multiple @change="selectFile" />
    </label>

    <button class="btn btn-success"
            :disabled="!selectedFiles"
            @click="uploadFiles"
    >
      Upload
    </button>

    <div v-if="progressInfos">
      <div class="mb-2"
           v-for="(progressInfo, index) in progressInfos"
           :key="index"
      >
        <span>{{progressInfo.fileName}}</span>
        <div class="progress">
          <div class="progress-bar progress-bar-info"
               role="progressbar"
               :aria-valuenow="progressInfo.percentage"
               aria-valuemin="0"
               aria-valuemax="100"
               :style="{ width: progressInfo.percentage + '%' }"
          >
            {{progressInfo.percentage}}%
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<style >

</style>