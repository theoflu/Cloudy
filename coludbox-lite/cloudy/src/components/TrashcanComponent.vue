<script >

import {defineComponent} from "vue";
import HelloWorld from "@/components/HelloWorld.vue";
import {getAll} from "@/common/user-service";
import axios from "axios";
import {FwbDropdown, FwbListGroup, FwbListGroupItem} from "flowbite-vue";



export default defineComponent({
  components: { HelloWorld,FwbListGroupItem, FwbListGroup, FwbDropdown},
  name: 'TrashcanComponent',
  el: '.checkbox',
  props: {
    msg: String
  },
  data() {
    return {
      files: [],
      jwt: "",
      durum: [],
      datsa: {
        filename:"",
        isCheck:false,

      }, customHeaders : {
        'Authorization': "Bearer " + this.jwt,

      }



    }
  },
  created() {
    this.jwt = localStorage.getItem('accessToken');
    const customHeaders = {
      'Authorization': "Bearer " + this.jwt,

    };

    this.getfilelists(customHeaders);



  }, methods: {

    getfilelists(customHeaders) {

      getAll(customHeaders).then(response => {
        this.files = response.data;
        console.log( this.files);
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

    }}
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
                      <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>

                    <tr v-for='(item, index) in files' :key='index'>

                      <td v-if="item.trashCanFiles==true">
                        <div class="d-flex align-items-center">
                          <div class="mr-3">
                            <a href="#"><img src="../../../assets/images/layouts/page-6/01.png" class="img-fluid avatar-30"  alt="image1"></a>
                          </div>
                          {{item.filename}}
                        </div>
                      </td >
                      <td v-if="item.trashCanFiles==true" > ?????</td>
                      <td  v-if="item.trashCanFiles==true">jan 21, 2020 me</td>
                      <td v-if="item.trashCanFiles==true">{{item.fileDetail.filesize/1000000000}} GB</td>
                      <td  v-if="item.trashCanFiles==true">
                        <fwb-dropdown text="Bottom">
                          <template #trigger>
      <span class="dropdown-toggle" id="dropdownMenuButton6" data-toggle="dropdown" aria-expanded="false">
        <i class="ri-more-fill"></i>
      </span>
                          </template>
                          <fwb-list-group class="fwb-list-group">
                            <fwb-list-group-item  class="fwb-list-group-item" @click="moveToTrash(item.filename)">
                              Rescue File
                            </fwb-list-group-item>
                            <fwb-list-group-item  class="fwb-list-group-item" @click="deleteFile(item.filename)">
                              Delete
                            </fwb-list-group-item>
                          </fwb-list-group>
                        </fwb-dropdown>
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