<script >

import {getAll} from "@/common/user-service";
import axios from "axios";
import HelloWorld from "@/components/HelloWorld.vue";
import DropFile from "@/components/DropFile.vue";
import {Fancybox} from "@fancyapps/ui";
import PdfViewer from "@/components/PdfViewer.vue";





export default {
  name: 'MainMenu',
  components: {PdfViewer, DropFile, HelloWorld},
  el: '.checkbox',
  props: {
    msg: String
  },
  data() {
    return {
      files: [],
      receivedMessage: "",
      picFileUrls: [], // Dosya URL'lerini saklamak için bir dizi
      vidFileUrls: [],
      FileUrls: [],
      searchTerm: '',
      urls:[],
      jwt: "",
      wholePacks: [],
      datsa: {
        filename:"",
        isCheck:false,

      },
      isActive: false,
      name: 'demo.pdf',
      path: 'lib/web/viewer.html',
      modalShow: false,
      itemsPerPage: 8, // Her sayfada gösterilecek öğe sayısı
      currentPage: 1, // Şu anki sayfa numarası

    }
  },
  created() {
    this.jwt = localStorage.getItem('accessToken');
    const customHeaders = {
      'Authorization': "Bearer " + this.jwt,

    };

    this.getfilelists(customHeaders);

  },
  computed: {
    // Mevcut sayfadaki öğeler
    displayedItems() {
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      const endIndex = startIndex + this.itemsPerPage;


      //return this.picFileUrls.concat(this.vidFileUrls, this.FileUrls).slice(startIndex, endIndex);
      return this.urls.filter(item =>
          item.filename.toLowerCase().includes(this.searchTerm.toLowerCase())
      ).slice(startIndex, endIndex);

    },
    filteredItems() {
      // searchTerm'e göre filtreleme işlemi
      return this.urls.filter(item =>
          item.filename.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    },
    // Toplam sayfa sayısı
    pages() {
      const totalLength = this.urls.length;
      return Math.ceil(totalLength / this.itemsPerPage);
    },
  },
  methods: {
    handleSearchTermChange(searchTerm) {
      this.searchTerm = searchTerm; // Arama terimini ana bileşende güncelliyoruz
    },
    handlemessage(message){
      console.log(message);
      this.files=message;
      this.picFileUrls=[];
      this.urls=[];
      this.vidFileUrls=[];
      this.FileUrls=[];
      this.updateFiles(message);
    },

    async updateFiles(data) {
      try {

        this.files = data;

        // Paralel olarak istekleri yap
        await Promise.all(this.files
            .filter(file => !file.trashCanFiles)
            .map(file => this.ListFiles(file.filename, file.fileDetail.mediType, file)));

      } catch (error) {
        console.error('Dosya listesi alınamadı:', error);
      }
    }
    ,

    truncateString(str, maxLength) {
      // Eğer dize belirtilen maksimum uzunluktan uzunsa, kısalt
      if (str.length > maxLength) {
        return str.slice(0, maxLength) + '...'; // Kısaltılmış dizeyi ve üç noktayı ekle
      } else {
        return str; // Maksimum uzunluğu geçmiyorsa, dizeyi değiştirmeden geri döndür
      }
    },
    async getfilelists(customHeaders) {
      try {
        this.files=[];
        this.picFileUrls=[];
        this.vidFileUrls=[];
        this.FileUrls=[];

        const response = await getAll(customHeaders);
        this.files = response.data;

        // Paralel olarak istekleri yap
        await Promise.all(this.files
            .filter(file => !file.trashCanFiles)
            .map(file => this.ListFiles(file.filename, file.fileDetail.mediType, file)));

      } catch (error) {
        console.error('Dosya listesi alınamadı:', error);
      }
    }
    ,

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
    async reduceImageQuality(blob, quality) {
      return new Promise((resolve, reject) => {
        const img = new Image();
        img.crossOrigin = 'anonymous'; // Eğer farklı bir origin'den resim çekiliyorsa bu gerekli olabilir
        img.onload = function () {
          const canvas = document.createElement('canvas');
          canvas.width = img.width;
          canvas.height = img.height;
          const ctx = canvas.getContext('2d');

          // Resmi canvas'a çiz
          ctx.drawImage(img, 0, 0);

          // Kaliteyi düşürerek canvas'tan yeni bir Blob oluştur
          canvas.toBlob(
              (newBlob) => {
                resolve(newBlob);
              },
              'image/jpeg', // veya 'image/png'
              quality // Kalite seviyesi (0 ile 1 arasında bir değer)
          );
        };

        img.onerror = (error) => reject(error);

        img.src = URL.createObjectURL(blob);
      });
    }
    ,
    async ListFiles(filename, mediType, file) {
      try {
        const response = await axios.get(`user/download/${filename}`, {
          headers: {
            'Authorization': `Bearer ${this.jwt}`,
          },
          responseType: 'blob',
        });

        const url = URL.createObjectURL(response.data);

        if (mediType === "image/jpeg" || mediType === "image/png") {
          const reducedBlob = await this.reduceImageQuality(response.data, 0.2);
          const reducedUrl = URL.createObjectURL(reducedBlob);

          this.urls.push({ filename, url:reducedUrl, file ,type:"pic"});
        } else if(mediType === "image/gif") {
          this.urls.push({ filename, url, file ,type:"pic"});

        }
          else if (mediType === "video/mp4" ) {
          this.urls.push({ filename, url, file ,type:"vid"});
        }
          else if ( mediType==="audio/mpeg" ) {
          const url = URL.createObjectURL(response.data);

          this.urls.push({ filename, url, file ,type:"mzk"});
        }
          else {
          const fileExtension = filename.split('.').pop().toLowerCase();
          this.urls.push({ filename, url, file, fileExtension ,type:"file"});
        }
      } catch (error) {
        console.error('Dosya alınamadı:', error);
      }
    }
    ,
    getFileUrls(filename, fileType) {
      let fileUrls = (fileType === 'image') ? this.picFileUrls : (fileType === 'video') ? this.vidFileUrls : (fileType === 'file') ? this.FileUrls : [];
      const file = fileUrls.find(file => file.filename === filename);
      return file ? file.url : null;
    },
    async getFiles(filename) {
      try {
        //TODO main menu dosya alma şeklini bir değiştirelim bakalım hızlanacak mı sonra arama yerini yap

        const response = await axios.get(`user/download/${filename}`, {
          headers: {
            'Authorization': "Bearer " + this.jwt,

          },
          responseType: 'blob' // Dosyayı binary olarak almak için blob kullanılır
        });
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
    getImgUrl2(filename) {
      try {
        const fileExtension = filename.split('.').pop().toLowerCase();

        var images = require.context('../../../assets/images/layouts/page-1/', false, /\.png$/)
        if(fileExtension==="png" || fileExtension=== "jpg"|| fileExtension==="mp4" || fileExtension === "jfif"|| fileExtension === "gif" || fileExtension==="mp3" || fileExtension==="jpeg"){

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
    async checkCheckbox(item) {

      this.datsa.filename=item.filename;
      this.datsa.isCheck=item.file.favourite;
      await axios.post(`user/favourite`, this.datsa, {
        headers: {
          'Authorization': "Bearer " + this.jwt,

        },
      });


    }
    ,  async moveToTrash(filename) {
      try {
        const response = await axios.get(`user/movetotrash/${filename}`, {
          headers: {
            'Authorization': "Bearer " + this.jwt,
          },
        });
        this.files = response.data;
        const picIndex = this.picFileUrls.findIndex(file => file.filename === filename);
        if (picIndex !== -1) {
          this.picFileUrls.splice(picIndex, 1);
        }

        const vidIndex = this.vidFileUrls.findIndex(file => file.filename === filename);
        if (vidIndex !== -1) {
          this.vidFileUrls.splice(vidIndex, 1);
        }

        const fileIndex = this.FileUrls.findIndex(file => file.filename === filename);
        if (fileIndex !== -1) {
          this.FileUrls.splice(fileIndex, 1);
        }

      } catch (error) {
        console.error(error);
      }

    },
    getImgUrl(pet) {
      try {
        var images = require.context('../../../assets/images/layouts/page-1/', false, /\.png$/)
        return images('./' + pet + ".png")
      }
      catch(error){
        return images('./' + "default" + ".png")
      }
    },
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
  }
  ,
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
  <HelloWorld @search-changed="handleSearchTermChange"/>
  <div class="content-page">
    <div class="container-fluid">
      <div class="row">
        <div class="col-lg-12">
          <div class="card-transparent card-block card-stretch card-height mb-3">
            <div class="d-flex justify-content-between">
              <div class="select-dropdown input-prepend input-append">
                <div class="btn-group">
                  <div data-toggle="dropdown">
                    <div class="dropdown-toggle search-query">My Drive<i class="las la-angle-down ml-3"></i></div><span class="search-replace"></span>
                    <span class="caret"><!--icon--></span>
                  </div>
                  <ul class="dropdown-menu">
                    <li><div class="item"><i class="ri-folder-add-line pr-3"></i>New Folder</div></li>
                    <li><div class="item"><i class="ri-file-upload-line pr-3"></i>Upload Files</div></li>
                    <li><div class="item"><i class="ri-folder-upload-line pr-3"></i>Upload Folders</div></li>
                  </ul>
                </div>
              </div>
              <div class="dashboard1-dropdown d-flex align-items-center">
                <div class="dashboard1-info">
                  <a href="#calander" class="collapsed" data-toggle="collapse" aria-expanded="false">
                    <i class="ri-arrow-down-s-line"></i>
                  </a>
                  <ul id="calander" class="iq-dropdown collapse list-inline m-0 p-0 mt-2">
                    <li class="mb-2">
                      <a href="#" data-toggle="tooltip" data-placement="right" title="Calander"><i
                          class="las la-calendar iq-arrow-left"></i></a>
                    </li>
                    <li class="mb-2">
                      <a href="#" data-toggle="tooltip" data-placement="right" title="Keep"><i
                          class="las la-lightbulb iq-arrow-left"></i></a>
                    </li>
                    <li>
                      <a href="#" data-toggle="tooltip" data-placement="right" title="Tasks"><i
                          class="las la-tasks iq-arrow-left"></i></a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-8 ">
          <div class="card card-block card-stretch card-height iq-welcome" >
            <div class="card-body property2-content">
              <div class="d-flex flex-wrap align-items-center">
                <div class="col-lg-8 col-sm-8 p-0">
                  <DropFile @message="handlemessage"/>

                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-lg-4">
          <div class="card card-block card-stretch card-height">
            <div class="card-header d-flex justify-content-between">
              <div class="header-title">
                <h4 class="card-title">Quick Access</h4>
              </div>
            </div>
            <div class="card-body">

            </div>
          </div>
        </div>
        <div class="col-lg-12">
          <div class="card card-block card-stretch card-transparent ">
            <div class="card-header d-flex justify-content-between pb-0">
              <div class="header-title">
                <h4 class="card-title">Documents</h4>
              </div>
              <div class="card-header-toolbar d-flex align-items-center">
                <a href="./page-folders.html" class=" view-more">View All</a>
              </div>
            </div>
          </div>
        </div>
        <div  class="col-lg-3 col-md-6 col-sm-6" v-for='(item, index) in displayedItems' :key='index' >

          <div  v-if="item.type=='pic'" >
            <div class="card card-block card-stretch card-height" v-if="!item.file.trashCanFile">
              <div class="" style="rotation:inherit">
                <!--
                <fwb-dropdown text="bottom">
                  <template #trigger>
        <span class="dropdown-toggle" id="dropdownMenuButton6" data-toggle="dropdown" aria-expanded="false">
          <i class="ri-more-fill"></i>
        </span>
                  </template>
                  <fwb-list-group class="fwb-list-group">
                    <fwb-list-group-item  class="fwb-list-group-item" @click="moveToTrash(item.filename)">
                      Move To Trash
                    </fwb-list-group-item>
                    <fwb-list-group-item  class="fwb-list-group-item" @click="getFiles(item.filename)">
                      Download
                    </fwb-list-group-item>
                  </fwb-list-group>
                </fwb-dropdown>
  -->

              </div>
              <div class=" card-body image-thumb">
                <table >
                  <tr>
                    <td style="vertical-align: top;">
                      <input class="checkbox" type="checkbox" :id="item.filename" v-model="item.file.favourite" @change="checkCheckbox(item)" />
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
                      </label>
                    </td>
                    <td style="vertical-align: top;">
                      <a class="deletebtn" @click="moveToTrash(item.filename)">
                        <i class="las la-trash-alt iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                    <td style="vertical-align: top;">
                      <a class="deletebtn" @click="getFiles(item.filename)">
                        <i class="las la-cloud-download-alt iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                  </tr>
                </table>
                <a href="#" :data-title=item.filename data-load-file="file" data-load-target="#resolte-contaniner" :data-url="item.filepath" data-toggle="modal" data-target="#exampleModal">
                  <div class="mb-4 text-center p-3 rounded iq-thumb">
                    <div class="iq-image-overlay"></div>
                    <img height="200" width="300" :src="getImgUrl2(item.filename)" data-fancybox="gallery" class="img-fluid" alt="image1"/>
                  </div>
                  <h6 ref="ad">{{item.filename}}</h6>
                </a>
              </div>

            </div>
          </div>
          <div  v-else-if="item.type=='vid'">
            <div class="card card-block card-stretch card-height" v-if="!item.file.trashCanFile">
              <div class="card-body image-thumb">
                <div class="" style="rotation: revert">
                </div>
                <table >
                  <tr>
                    <td style="vertical-align: top;">
                      <input class="checkbox" type="checkbox" :id="item.filename" v-model="item.file.favourite" @change="checkCheckbox(item)" />
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
                      </label>
                    </td>
                    <td style="vertical-align: top;">
                      <a class="deletebtn" @click="moveToTrash(item.filename)">
                        <i class="las la-trash-alt iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                    <td style="vertical-align: top;">
                      <a class="deletebtn" @click="getFiles(item.filename)">
                        <i class="las la-cloud-download-alt iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                  </tr>
                </table>
                <a href="#" :data-title=item.filename data-load-file="file" data-load-target="#resolte-contaniner" :data-url="item.filepath" data-toggle="modal" data-target="#exampleModal">
                  <div class="mb-4 text-center p-3 rounded iq-thumb">
                    <div class="iq-image-overlay"></div>
                    <video height="100" width="170" controls controlsList="nodownload">
                      <source :src="getImgUrl2(item.filename)" type="video/mp4" >
                    </video>
                  </div>
                  <h6 ref="ad">{{item.filename}}</h6>
                </a>
              </div>
            </div>
          </div>
          <div  v-else-if="item.type=='mzk'">
            <div class="card card-block card-stretch card-height" v-if="!item.file.trashCanFile">
              <div class="card-body image-thumb">
                <div class="" style="rotation: revert">
                </div>
                <table >
                  <tr>
                    <td style="vertical-align: top;">
                      <input class="checkbox" type="checkbox" :id="item.filename" v-model="item.file.favourite" @change="checkCheckbox(item)" />
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
                      </label>
                    </td>
                    <td style="vertical-align: top;">
                      <a class="deletebtn" @click="moveToTrash(item.filename)">
                        <i class="las la-trash-alt iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                    <td style="vertical-align: top;">
                      <a class="deletebtn" @click="getFiles(item.filename)">
                        <i class="las la-cloud-download-alt iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                  </tr>
                </table>
                <a href="#" :data-title=item.filename data-load-file="file" data-load-target="#resolte-contaniner" :data-url="item.filepath" data-toggle="modal" data-target="#exampleModal">
                  <div class="mb-4 text-center p-3 rounded iq-thumb">
                    <div class="iq-image-overlay"></div>
                    <audio style="width: 200px;height:100px " controls controlsList="nodownload">
                      <source :src="getImgUrl2(item.filename)" type="audio/mpeg">
                    </audio>
                  </div>
                  <h6 ref="ad">{{item.filename}}</h6>
                </a>
              </div>
            </div>
          </div>

          <div v-else >
            <div class="card card-block card-stretch card-height" v-if="!item.file.trashCanFile">
              <div class="card-body image-thumb">
                <div class="" style="rotation: revert">
                </div>
                <table >
                  <tr>
                    <td style="vertical-align: top;">
                      <input class="checkbox" type="checkbox" :id="item.filename" v-model="item.file.favourite" @change="checkCheckbox(item)" />
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
                      </label>
                    </td>
                    <td style="vertical-align: top;">
                      <a class="deletebtn" @click="moveToTrash(item.filename)">
                        <i class="las la-trash-alt iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                    <td style="vertical-align: top;">
                      <a class="deletebtn" @click="getFiles(item.filename)">
                        <i class="las la-cloud-download-alt iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                    <td v-if="item.fileExtension=='pdf'" style="vertical-align: top;">
                      <a class="deletebtn" @click="openModal(item.filename)">
                        <i class="lab la-readme iq-arrow-left" style="font-size: 38px;"></i>
                      </a>
                    </td>
                  </tr>
                </table>

                <a href="#" :data-title=item.filename data-load-file="file" data-load-target="#resolte-contaniner" :data-url="item.filepath" data-toggle="modal" data-target="#exampleModal">
                  <div class="mb-4 text-center p-3 rounded iq-thumb">
                    <div class="iq-image-overlay"></div>

                    <img :src="getImgUrl(item.fileExtension)" class="img-fluid" alt="image1">

                  </div>
                  <h6 ref="ad">{{truncateString(item.filename,14)}}</h6>
                </a>
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
        <div class="col-lg-12">
          <div class="card card-block card-stretch card-transparent">
            <div class="card-header d-flex justify-content-between pb-0">
              <div class="header-title">
                <h4 class="card-title">Folders</h4>
              </div>
              <div class="card-header-toolbar d-flex align-items-center">
                <div class="dropdown">
                                    <span class="dropdown-toggle dropdown-bg btn bg-white" id="dropdownMenuButton1"
                                          data-toggle="dropdown">
                                        Name<i class="ri-arrow-down-s-line ml-1"></i>
                                    </span>
                  <div class="dropdown-menu dropdown-menu-right shadow-none"
                       aria-labelledby="dropdownMenuButton1">
                    <a class="dropdown-item" href="#">Last modified</a>
                    <a class="dropdown-item" href="#">Last modifiedby me</a>
                    <a class="dropdown-item" href="#">Last opened by me</a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6 col-sm-6 col-lg-3">
          <div class="card card-block card-stretch card-height">
            <div class="card-body">
              <div class="d-flex justify-content-between">

                <a href="./page-alexa.html" class="folder">
                  <div class="icon-small bg-danger rounded mb-4">
                    <i class="ri-file-copy-line"></i>
                  </div>
                </a>
                <div class="card-header-toolbar">
                  <div class="dropdown">
                                            <span class="dropdown-toggle" id="dropdownMenuButton2" data-toggle="dropdown">
                                                <i class="ri-more-2-fill"></i>
                                            </span>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton2">
                      <a class="dropdown-item" href="#"><i class="ri-eye-fill mr-2"></i>View</a>
                      <a class="dropdown-item" href="#"><i class="ri-delete-bin-6-fill mr-2"></i>Delete</a>
                      <a class="dropdown-item" href="#"><i class="ri-pencil-fill mr-2"></i>Edit</a>
                      <a class="dropdown-item" href="#"><i class="ri-printer-fill mr-2"></i>Print</a>
                      <a class="dropdown-item" href="#"><i class="ri-file-download-fill mr-2"></i>Download</a>
                    </div>
                  </div>
                </div>
              </div>
              <a href="./page-alexa.html" class="folder">
                <h5 class="mb-2">Alexa Workshop</h5>
                <p class="mb-2"><i class="lar la-clock text-danger mr-2 font-size-20"></i> 10 Dec, 2020</p>
                <p class="mb-0"><i class="las la-file-alt text-danger mr-2 font-size-20"></i> 08 Files</p>
              </a>
            </div>
          </div>
        </div>

        <div class="col-lg-8 col-xl-8">
          <div class="card card-block card-stretch card-height files-table">
            <div class="card-header d-flex justify-content-between">
              <div class="header-title">
                <h4 class="card-title">Files</h4>
              </div>
              <div class="card-header-toolbar d-flex align-items-center">
                <a href="./page-files.html" class=" view-more">View All</a>
              </div>
            </div>
            <div class="card-body pt-0">
              <div class="table-responsive">
                <table class="table mb-0 table-borderless tbl-server-info">
                  <thead>
                  <tr>
                    <th scope="col">Name</th>
                    <th scope="col">Members</th>
                    <th scope="col">Last Edit</th>
                    <th scope="col">Size</th>
                    <th scope="col"></th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr>
                    <td>
                      <div class="d-flex align-items-center">
                        <div class="icon-small bg-danger rounded mr-3">
                          <i class="ri-file-excel-line"></i>
                        </div>
                        <div data-load-file="file" data-load-target="#resolte-contaniner" data-url="../../../assets/vendor/doc-viewer/files/demo.pdf" data-toggle="modal" data-target="#exampleModal" data-title="Weekly-report.pdf" style="cursor: pointer;">Weekly-report.pdf</div>
                      </div>
                    </td>
                    <td>Me</td>
                    <td>jan 21, 2020 me</td>
                    <td>02 MB</td>
                    <td>
                      <div class="dropdown">
                                                <span class="dropdown-toggle" id="dropdownMenuButton6" data-toggle="dropdown">
                                                    <i class="ri-more-fill"></i>
                                                </span>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton6">
                          <a class="dropdown-item" href="#"><i class="ri-eye-fill mr-2"></i>View</a>
                          <a class="dropdown-item" href="#"><i class="ri-delete-bin-6-fill mr-2"></i>Delete</a>
                          <a class="dropdown-item" href="#"><i class="ri-pencil-fill mr-2"></i>Edit</a>
                          <a class="dropdown-item" href="#"><i class="ri-printer-fill mr-2"></i>Print</a>
                          <a class="dropdown-item" href="#"><i class="ri-file-download-fill mr-2"></i>Download</a>
                        </div>
                      </div>
                    </td>
                  </tr>
                  <tr class="active">
                    <td>
                      <div class="d-flex align-items-center">
                        <div class="icon-small bg-primary rounded mr-3">
                          <i class="ri-file-download-line"></i>
                        </div>
                        <div data-load-file="file" data-load-target="#resolte-contaniner" data-url="../../../assets/vendor/doc-viewer/files/demo.pdf" data-toggle="modal" data-target="#exampleModal" data-title="VueJs.pdf" style="cursor: pointer;">VueJs.pdf</div>
                      </div>
                    </td>
                    <td>Poul Molive</td>
                    <td>jan 25, 2020 Poul Molive</td>
                    <td>64 MB</td>
                    <td>
                      <div class="dropdown">
                                                <span class="dropdown-toggle" id="dropdownMenuButton7" data-toggle="dropdown">
                                                    <i class="ri-more-fill"></i>
                                                </span>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton7">
                          <a class="dropdown-item" href="#"><i class="ri-eye-fill mr-2"></i>View</a>
                          <a class="dropdown-item" href="#"><i class="ri-delete-bin-6-fill mr-2"></i>Delete</a>
                          <a class="dropdown-item" href="#"><i class="ri-pencil-fill mr-2"></i>Edit</a>
                          <a class="dropdown-item" href="#"><i class="ri-printer-fill mr-2"></i>Print</a>
                          <a class="dropdown-item" href="#"><i class="ri-file-download-fill mr-2"></i>Download</a>
                        </div>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div class="d-flex align-items-center">
                        <div class="icon-small bg-info rounded mr-3">
                          <i class="ri-file-excel-line"></i>
                        </div>
                        <div data-load-file="file" data-load-target="#resolte-contaniner" data-url="../../../assets/vendor/doc-viewer/files/demo.docx" data-toggle="modal" data-target="#exampleModal" data-title="Milestone.docx" style="cursor: pointer;">Milestone.docx</div>
                      </div>
                    </td>
                    <td>Me</td>
                    <td>Mar 30, 2020 Gail Forcewind</td>
                    <td>30 MB</td>
                    <td>
                      <div class="dropdown">
                                                <span class="dropdown-toggle" id="dropdownMenuButton8" data-toggle="dropdown">
                                                    <i class="ri-more-fill"></i>
                                                </span>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton8">
                          <a class="dropdown-item" href="#"><i class="ri-eye-fill mr-2"></i>View</a>
                          <a class="dropdown-item" href="#"><i class="ri-delete-bin-6-fill mr-2"></i>Delete</a>
                          <a class="dropdown-item" href="#"><i class="ri-pencil-fill mr-2"></i>Edit</a>
                          <a class="dropdown-item" href="#"><i class="ri-printer-fill mr-2"></i>Print</a>
                          <a class="dropdown-item" href="#"><i class="ri-file-download-fill mr-2"></i>Download</a>
                        </div>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div class="d-flex align-items-center">
                        <div class="icon-small bg-success rounded mr-3">
                          <i class="ri-file-excel-line"></i>
                        </div>
                        <div data-load-file="file" data-load-target="#resolte-contaniner" data-url="../../../assets/vendor/doc-viewer/files/demo.xlsx" data-toggle="modal" data-target="#exampleModal" data-title="Training center.xlsx" style="cursor: pointer;">Training center.xlsx</div>
                      </div>
                    </td>
                    <td>Me</td>
                    <td>Mar 30, 2020 Gail Forcewind</td>
                    <td>10 MB</td>
                    <td>
                      <div class="dropdown">
                                                <span class="dropdown-toggle" id="dropdownMenuButton09" data-toggle="dropdown">
                                                    <i class="ri-more-fill"></i>
                                                </span>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton09">
                          <a class="dropdown-item" href="#"><i class="ri-eye-fill mr-2"></i>View</a>
                          <a class="dropdown-item" href="#"><i class="ri-delete-bin-6-fill mr-2"></i>Delete</a>
                          <a class="dropdown-item" href="#"><i class="ri-pencil-fill mr-2"></i>Edit</a>
                          <a class="dropdown-item" href="#"><i class="ri-printer-fill mr-2"></i>Print</a>
                          <a class="dropdown-item" href="#"><i class="ri-file-download-fill mr-2"></i>Download</a>
                        </div>
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <div class="d-flex align-items-center">
                        <div class="icon-small bg-warning rounded mr-3">
                          <i class="ri-file-excel-line"></i>
                        </div>
                        <div data-load-file="file" data-load-target="#resolte-contaniner" data-url="../../../assets/vendor/doc-viewer/files/demo.pptx" data-toggle="modal" data-target="#exampleModal" data-title="Flavour.pptx" style="cursor: pointer;">Flavour.pptx</div>
                      </div>
                    </td>
                    <td>Me</td>
                    <td>Mar 30, 2020 Gail Forcewind</td>
                    <td>10 MB</td>
                    <td>
                      <div class="dropdown">
                                                <span class="dropdown-toggle" id="dropdownMenuButton9" data-toggle="dropdown">
                                                    <i class="ri-more-fill"></i>
                                                </span>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton9">
                          <a class="dropdown-item" href="#"><i class="ri-eye-fill mr-2"></i>View</a>
                          <a class="dropdown-item" href="#"><i class="ri-delete-bin-6-fill mr-2"></i>Delete</a>
                          <a class="dropdown-item" href="#"><i class="ri-pencil-fill mr-2"></i>Edit</a>
                          <a class="dropdown-item" href="#"><i class="ri-printer-fill mr-2"></i>Print</a>
                          <a class="dropdown-item" href="#"><i class="ri-file-download-fill mr-2"></i>Download</a>
                        </div>
                      </div>
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
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