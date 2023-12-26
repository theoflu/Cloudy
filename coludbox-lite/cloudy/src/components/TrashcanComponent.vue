<script >

import {defineComponent} from "vue";
import HelloWorld from "@/components/HelloWorld.vue";
import {getTrashFile} from "@/common/user-service";
import axios from "axios";
import {Fancybox} from "@fancyapps/ui";



export default defineComponent({
  components: { HelloWorld,},
  name: 'TrashcanComponent',
  el: '.checkbox',
  props: {
    msg: String
  },
  data() {
    return {
      files: [],
      jwt: "",
      urls: [],
      datsa: {
        filename:"",
        isCheck:false,

      }, customHeaders : {
        'Authorization': "Bearer " + this.jwt,

      },
      itemsPerPage: 6, // Her sayfada gösterilecek öğe sayısı
      currentPage: 1, // Şu anki sayfa numarası



    }
  },computed: {
    // Mevcut sayfadaki öğeler
    displayedItems() {
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      const endIndex = startIndex + this.itemsPerPage;


      return this.files.slice(startIndex, endIndex);
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
      try {
        getTrashFile(customHeaders).then(response => {
          this.files = response.data;

          for(var i=0;i<this.files.length;i++){
            this.getFileUrl(this.files[i].filename)
          }

        })

      }
      catch (error) {
        console.error('Dosya listesi alınamadı:', error);
      }
    }
  ,
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
    }, async deleteFile(filename){
      const response = await axios.get(`user/delete/${filename}`, {
        headers: {
          'Authorization': "Bearer " + this.jwt,
        },
      });
      console.log(response);
      this.files = response.data;

    }
,    async moveToTrash(filename) {
      try {
        const response = await axios.get(`user/movetotrash/${filename}`, {
          headers: {
            'Authorization': "Bearer " + this.jwt,
          },
        });
        console.log(response);
        this.files = response.data;

      } catch (error) {
        console.error(error);
      }

    },
     async getFileUrl(filename){
        const response =  await axios.get(`user/showtrashcan/${filename}`, {
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
      getImgUrl(filename) {
      try {
        const fileExtension = filename.split('.').pop().toLowerCase();

        var images = require.context('../../../assets/images/layouts/page-1/', false, /\.png$/)
        if(fileExtension==="png" || fileExtension=== "jpg"){

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
    }

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
  <HelloWorld/>

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
                      <th scope="col">???</th>
                      <th scope="col">Last Edit</th>
                      <th scope="col">File Size</th>
                      <th scope="col">Rescue File</th>
                      <th scope="col">Delete</th>
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
                      <td  > ?????</td>
                      <td  >jan 21, 2020 me</td>
                      <td >{{item.fileDetail.filesize/1000000000}} GB</td>
                      <td style="vertical-align: top;" >
                        <a class="deletebtn" @click="moveToTrash(item.filename)">
                          <i class="las la-trash-restore" style="font-size: 38px;"></i>
                        </a>
                      </td>
                          <td style="vertical-align: top;">
                            <a class="deletebtn" @click="deleteFile(item.filename)">
                              <i class="las la-trash-alt iq-arrow-left" style="font-size: 38px;"></i>
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

    </div>
  </div>

</template>

<style>
.dropdown-toggle {
  /* Dropdown tetikleyici öğesi için özel stiller */

}

.fwb-list-group {
  /* Öğe listesi bileşeni için özel stiller */
  color: gold;
  cursor: grab;

}


.fwb-list-group-item {
  display: block;
  width: 100%;
  padding: 0.25rem 1.5rem;
  clear: both;
  font-weight: 400;
  color: #212529;
  text-align: inherit;
  white-space: nowrap;
  background-color: transparent;
  border: 0; }
.fwb-list-group-item:hover, .fwb-list-group-item:focus {
  color: #16181b;
  text-decoration: none;
  background-color: #f8f9fa; }
.fwb-list-group-item.active, .fwb-list-group-item:active {
  color: #fff;
  text-decoration: none;
  background-color: #8f93f6; }
.fwb-list-group-item.disabled, .fwb-list-group-item:disabled {
  color: #6c757d;
  pointer-events: none;
  background-color: transparent; }

</style>