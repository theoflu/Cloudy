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
      picFileUrls: [], // Dosya URL'lerini saklamak için bir dizi
      vidFileUrls: [],
      FileUrls: [],
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
        console.log(this.files);
        this.files.forEach(file => {
          this.getFiles(file.filename,file.fileDetail.mediType); // Her dosya için getFiles metodunu çağır
        });
      })
    },
    async getFiles(filename,mediType) {
      try {
        const response = await axios.get(`user/download/${filename}`, {
          headers: {
            'Authorization': "Bearer " + this.jwt,
          },
          responseType: 'blob'
        });

        const url = window.URL.createObjectURL(new Blob([response.data]));
        if(mediType=="image/jpeg" || mediType=="image/png")
        {
          this.picFileUrls.push({ filename, url }); // Dosya adı ve URL'sini bir nesne olarak ekle
        }
        else if(mediType=="video/mp4") {
          this.vidFileUrls.push({ filename, url }); // Dosya adı ve URL'sini bir nesne olarak ekle
        }
        else {
          this.FileUrls.push({ filename, url }); // Dosya adı ve URL'sini bir nesne olarak ekle
        }





      } catch (error) {
        throw new Error('Dosya alınamadı:', error);
      }
    },
    getFileUrl(filename, fileType) {
      let fileUrls = (fileType === 'image') ? this.picFileUrls : (fileType === 'video') ? this.vidFileUrls : (fileType === 'pdf') ? this.vidFileUrls : [];
      const file = fileUrls.find(file => file.filename === filename);
      return file ? file.url : null;
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
      <div v-for="(file) in picFileUrls" :key="file.filename" class="preview-card">
        <div>
          <label>{{ file.filename }}</label>
          <img height="200" width="200" :src="getFileUrl(file.filename,'image')" data-fancybox="gallery" data-fancybox-index="0"/>
        </div>

      </div>
      <div v-for="(file) in vidFileUrls" :key="file.filename" class="preview-card">
        <div>
          <label>{{ file.filename }}</label>
          <video height="200" width="200" controls controlsList="nodownload">
            <source :src="getFileUrl(file.filename,'video')" type="video/mp4" >
          </video>
        </div>
      </div>

      <div v-for="(file) in FileUrls" :key="file.filename" class="preview-card">
        <div>
          <label>{{ file.filename }}</label>
          <video height="200" width="200" controls controlsList="nodownload">
            <source :src="getFileUrl(file.filename,'video')" type="video/mp4" >
          </video>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
@import "@fancyapps/ui/dist/fancybox/fancybox.css";
</style>
