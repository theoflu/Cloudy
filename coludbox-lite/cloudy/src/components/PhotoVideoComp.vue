<script>
import { Fancybox } from "@fancyapps/ui";
import { getAll } from "@/common/user-service";
import axios from "axios";

export default {
  name: 'PhotoVideoComp',
  components: {},
  data() {
    return {
      files: [],
      fileUrls: [], // Dosya URL'lerini saklamak için bir dizi
    }
  },
  created() {
    this.jwt = localStorage.getItem('accessToken');
    const customHeaders = {
      'Authorization': "Bearer " + this.jwt,
    };
    this.getfilelists(customHeaders);
  },
  methods: {
    getfilelists(customHeaders) {
      getAll(customHeaders).then(response => {
        this.files = response.data;
        this.files.forEach(file => {
          this.getFiles(file.filename); // Her dosya için getFiles metodunu çağır
        });
      })
    },
    async getFiles(filename) {
      try {
        const response = await axios.get(`user/download/${filename}`, {
          headers: {
            'Authorization': "Bearer " + this.jwt,
          },
          responseType: 'blob'
        });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        this.fileUrls.push(url); // Dosya URL'ini diziye ekle
      } catch (error) {
        throw new Error('Dosya alınamadı:', error);
      }
    },
  },
  mounted() {
    Fancybox.bind('[data-fancybox="gallery"]', {
      Toolbar: {
        display: {
          left: ["infobar"],
          middle: [
            "zoomIn",
            "zoomOut",
            "toggle1to1",
            "rotateCCW",
            "rotateCW",
            "flipX",
            "flipY",
          ],
          right: ["slideshow", "thumbs", "close"],
        },
      },
    });
  },
}
</script>

<template>
  <div>
    <h2>Resim ve Video Galerisi</h2>

    <div class="preview-container mt-4" v-if="files.length">
      <div v-for="(file, index) in files" :key="file.filename" class="preview-card">
        <div>
          <img height="200" width="200" :src="fileUrls[index]" data-fancybox="gallery" data-fancybox-index="0"/>


        </div>
      </div>
    </div>
  </div>
</template>

<style>
@import "@fancyapps/ui/dist/fancybox/fancybox.css";
</style>
