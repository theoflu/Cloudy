<script >

import {defineComponent} from "vue";
import HelloWorld from "@/components/HelloWorld.vue";
import { getFavFile} from "@/common/user-service";
import axios from "axios";
import {Fancybox} from "@fancyapps/ui";
import PdfViewer from "@/components/PdfViewer.vue";



export default defineComponent({
  components: {PdfViewer, HelloWorld},
  name: 'FavouritePage',
  el: '.checkbox',
  props: {
    msg: String
  },
  data() {
    return {
      files: [],
      jwt: "",
      searchTerm: '',
      urls: [],
      modalShow: false,
      name: 'demo.pdf',
      path: 'lib/web/viewer.html',
      showRead:false,
      datsa: {
        filename:"",
        isCheck:false,
      }, customHeaders : {
        'Authorization': "Bearer " + this.jwt,

      },
      itemsPerPage: 6, // Her sayfada gösterilecek öğe sayısı
      currentPage: 1, // Şu anki sayfa numarası



    }
  },
  computed: {
    // Mevcut sayfadaki öğeler
    displayedItems() {
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      const endIndex = startIndex + this.itemsPerPage;


      //return this.files.slice(startIndex, endIndex);
      return this.files.filter(item =>
          item.filename.toLowerCase().includes(this.searchTerm.toLowerCase())
      ).slice(startIndex, endIndex);

    },
    // Toplam sayfa sayısı
    pages() {
      return Math.ceil(this.files.length / this.itemsPerPage);
    },
  },
  created() {
    this.jwt = localStorage.getItem('accessToken');
    const customHeaders = {
      'Authorization': "Bearer " + this.jwt,

    };

    this.getfilelists(customHeaders);



  }, methods: {
    handleSearchTermChange(searchTerm) {
      this.searchTerm = searchTerm; // Arama terimini ana bileşende güncelliyoruz
    },
    changePage(pageNumber) {
      this.currentPage = pageNumber;
    },
    previousPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < this.pages.length) {
        this.currentPage++;
      }
    },
    getfilelists(customHeaders) {

      getFavFile(customHeaders).then(response => {

        this.files = response.data;
        console.log( this.files);
        for(var i=0;i<this.files.length;i++){
          this.getFileUrl(this.files[i].filename)
        }

      })
    },
    async getFiles(filename) {
      try {
        const response = await axios.get(`user/download/${filename}`, {
          headers: {
            'Authorization': "Bearer " + this.jwt,

          },
          responseType: 'blob' // Dosyayı binary olarak almak için blob kullanılır
        });
        console.log(response);
        const url = window.URL.createObjectURL(new Blob([response.data]));

        // Dosyayı indirmek için bir <a> etiketi oluşturma ve tıklama işlemi
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', filename);
        link.setAttribute('target', '_blank');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        return response.data; // Dosya içeriğini istemciye döndür
      } catch (error) {
        throw new Error('Dosya alınamadı:', error);
      }
    },
    async checkCheckbox(item) {
      this.datsa.filename=item.filename;
      const response= await axios.post(`user/favourite`, this.datsa, {
        headers: {
          'Authorization': "Bearer " + this.jwt,

        },
      });
      this.files = response.data;


    },
    async moveToTrash(filename) {
      try {
        const response =  await axios.get(`user/movetotrash/${filename}`, {
          headers: {
            'Authorization': "Bearer " + this.jwt,
          },
        });

        this.files=response.data;

      } catch (error) {
        console.error(error);
      }

    },
    async getFileUrl(filename){
      const response =  await axios.get(`user/download/${filename}`, {
        headers: {
          'Authorization': `Bearer ${this.jwt}`,
        },
        responseType: 'blob',
      });
      console.log(response);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      this.urls.push({filename,url});
    }
    ,

    openModal(filename) {
      this.modalShow = true;
      for (var i=0;i<this.urls.length;i++){
        console.log(filename);
        if(this.urls[i].filename== filename){
          this.name=this.urls[i].url;
        }
      }
    },
    closeModal() {
      this.modalShow = false;
    },
    getImgUrl(filename) {
      try {
        const fileExtension = filename.split('.').pop().toLowerCase();

        var images = require.context('../../../assets/images/layouts/page-1/', false, /\.png$/)
        if(fileExtension==="png" || fileExtension=== "jpg" || fileExtension === "jfif"|| fileExtension === "gif"){

          for (var i=0;i<this.urls.length;i++){
            if(filename===this.urls[i].filename){
              return this.urls[i].url;
            }
          }
          //
        }
        return images('./' + fileExtension + ".png")

      }
      catch(error){
        return images('./' + "default" + ".png")
      }
    },
    getFileTypePdf(filename){
      try {
        const fileExtension = filename.split('.').pop().toLowerCase();
        if(fileExtension==="pdf") {
          this.showRead=true;
          return true;

        }
        else {

          return false;
        }
      }
      catch(error){
        return false
      }
    }
    /*getImgUrl(pet) {
      try {
        const fileExtension = pet.split('.').pop().toLowerCase();
        var images = require.context('../../../assets/images/layouts/page-1/', false, /\.png$/)
        return images('./' + fileExtension + ".png")
      }
      catch(error){
        return images('./' + "default" + ".png")
      }*/

  },

  mounted() {
    Fancybox.bind('[data-fancybox]', {
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

})

</script>

<template>
  <HelloWorld @search-changed="handleSearchTermChange"/>

  <div class="content-page">
    <div class="container-fluid">
      <div class="icon icon-grid i-grid">
        <div class="row">
          <div class="col-lg-12">
            <div class="card card-block card-stretch card-height">
              <div class="card-body">
                <div class="table-responsive" >
                  <table class="table mb-0 table-borderless tbl-server-info">
                    <thead>
                    <tr>
                      <th scope="col">Name</th>
                      <th scope="col">FAV</th>
                      <th scope="col">Last Edit</th>
                      <th scope="col">File Size</th>
                      <th scope="col">Move To Trash</th>
                      <th scope="col">Download</th>
                      <th scope="col" v-if=" showRead" >Read</th>
                    </tr>
                    </thead>
                    <tbody>

                    <tr v-for='(item, index) in displayedItems' :key='index'>

                      <td >
                        <div class="d-flex align-items-center">
                          <div class="mr-3">
                            <img style="width: 35px;height: 35px;" data-fancybox="gallery" :src="getImgUrl(item.filename)" class="img-fluid" alt="image1">

                          </div>
                         {{item.filename}}
                        </div>
                      </td >
                      <td >  <input class="checkbox" type="checkbox" :id="item.filename" v-if="item.favourite" v-model="item.favourite" @change="checkCheckbox(item)" />
                        <label :for="item.filename" >
                          <svg id="heart-svg" viewBox="467 392 58 57" xmlns="http://www.w3.org/2000/svg">
                            <g id="Group" fill="none" fill-rule="evenodd" transform="translate(467 392)">
                              <path d="M29.144 20.773c-.063-.13-4.227-8.67-11.44-2.59C7.63 28.795 28.94 43.256 29.143 43.394c.204-.138 21.513-14.6 11.44-25.213-7.214-6.08-11.377 2.46-11.44 2.59z" id="heart" fill="#AAB8C2"/>
                              <circle id="main-circ" fill="#E2264D" opacity="0" cx="29.5" cy="29.5" r="1.5"/>

                              <g id="grp7" opacity="0" transform="translate(7 6)">
                                <circle id="oval1" fill="#9CD8C3" cx="2" cy="6" r="2"/>
                                <circle id="oval2" fill="#8CE8C3" cx="5" cy="2" r="2"/>
                              </g>

                              <g id="grp6" opacity="0" transform="translate(0 28)">
                                <circle id="oval1" fill="#CC8EF5" cx="2" cy="7" r="2"/>
                                <circle id="oval2" fill="#91D2FA" cx="3" cy="2" r="2"/>
                              </g>

                              <g id="grp3" opacity="0" transform="translate(52 28)">
                                <circle id="oval2" fill="#9CD8C3" cx="2" cy="7" r="2"/>
                                <circle id="oval1" fill="#8CE8C3" cx="4" cy="2" r="2"/>
                              </g>

                              <g id="grp2" opacity="0" transform="translate(44 6)">
                                <circle id="oval2" fill="#CC8EF5" cx="5" cy="6" r="2"/>
                                <circle id="oval1" fill="#CC8EF5" cx="2" cy="2" r="2"/>
                              </g>

                              <g id="grp5" opacity="0" transform="translate(14 50)">
                                <circle id="oval1" fill="#91D2FA" cx="6" cy="5" r="2"/>
                                <circle id="oval2" fill="#91D2FA" cx="2" cy="2" r="2"/>
                              </g>

                              <g id="grp4" opacity="0" transform="translate(35 50)">
                                <circle id="oval1" fill="#F48EA7" cx="6" cy="5" r="2"/>
                                <circle id="oval2" fill="#F48EA7" cx="2" cy="2" r="2"/>
                              </g>

                              <g id="grp1" opacity="0" transform="translate(24)">
                                <circle id="oval1" fill="#9FC7FA" cx="2.5" cy="3" r="2"/>
                                <circle id="oval2" fill="#9FC7FA" cx="7.5" cy="2" r="2"/>
                              </g>
                            </g>
                          </svg>
                        </label></td>
                      <td  >jan 21, 2020 me</td>
                      <td >{{item.fileDetail.filesize/1000000000}} GB</td>
                      <td style="vertical-align: top;" >
                        <a class="deletebtn" @click="moveToTrash(item.filename)">
                          <i class="las la-trash-alt iq-arrow-left" style="font-size: 38px;"></i>
                        </a>
                      </td>
                      <td style="vertical-align: top;">
                        <a class="deletebtn" @click="getFiles(item.filename)">
                          <i class="las la-cloud-download-alt iq-arrow-left" style="font-size: 38px;"></i>
                        </a>
                      </td>
                      <td style="vertical-align: top;" v-if="getFileTypePdf(item.filename)">
                        <a class="deletebtn" @click="openModal(item.filename)">
                          <i class="lab la-readme" style="font-size: 38px;"></i>
                        </a>
                      </td>
                    </tr>

                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
          <div class="col-lg-12">
            <nav aria-label="Page navigation example">
              <ul class="pagination justify-content-center">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <a class="page-link" href="#" @click.prevent="previousPage">Previous</a>
                </li>
                <li class="page-item" v-for="page in pages" :key="page" :class="{ active: currentPage === page }">
                  <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
                </li>
                <li class="page-item" :class="{ disabled: currentPage === pages.length }">
                  <a class="page-link" href="#" @click.prevent="nextPage">Next</a>
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
      <div id="show">

        <!-- Popup Modal -->
        <transition  name="fade">
          <div v-if="modalShow" class="popup-modal">
            <div class="popup-modal-content">
              <button class="popup-close-btn" @click="closeModal">×</button>
              <div class="modal-header">
                <h5 class="modal-title">PDF Viewer</h5>
              </div>
              <div class="modal-body">
                <!-- PdfViewer bileşeni buraya gelecek -->
                <PdfViewer :path="path" :fileName="name" style="width: 100%; height: 420px;" />
              </div>
            </div>
          </div>
        </transition>
      </div>

    </div>
  </div>

</template>


<style scoped>

/* Popup modal stilleri */
.popup-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.popup-modal-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 5px;
  width: 90%; /* Genişlik değeri ayarlayabilirsiniz */
  height: 90%; /* Yükseklik değeri ayarlayabilirsiniz */
  max-width: 800px;
  max-height: 800px;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
}
.popup-close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}
.modal-header {
  text-align: center;
}
</style>