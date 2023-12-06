<script >

import {defineComponent} from "vue";
import HelloWorld from "@/components/HelloWorld.vue";
import {getAll} from "@/common/user-service";
import axios from "axios";
import {FwbDropdown, FwbListGroup, FwbListGroupItem} from "flowbite-vue";



export default defineComponent({
  components: { HelloWorld,FwbListGroupItem, FwbListGroup, FwbDropdown},
  name: 'FavouritePage',
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
    },
    async checkCheckbox(item) {

      this.datsa.filename=item.filename;
      await axios.post(`user/favourite`, this.datsa, {
        headers: {
          'Authorization': "Bearer " + this.jwt,

        },
      });


    }
  }
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
                      <th scope="col">FAV</th>
                      <th scope="col">Last Edit</th>
                      <th scope="col">File Size</th>
                      <th scope="col"></th>
                    </tr>
                    </thead>
                    <tbody>

                    <tr v-for='(item, index) in files' :key='index'>

                      <td v-if="item.favourite!=false">
                        <div class="d-flex align-items-center">
                          <div class="mr-3">
                            <a href="#"><img src="../../../assets/images/layouts/page-6/01.png" class="img-fluid avatar-30" alt="image1"></a>
                          </div>
                         {{item.filename}}
                        </div>
                      </td >
                      <td v-if="item.favourite!=false">  <input class="checkbox" type="checkbox" :id="item.filename" v-model="item.favourite" @change="checkCheckbox(item)" />
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
                      <td  v-if="item.favourite!=false">jan 21, 2020 me</td>
                      <td v-if="item.favourite!=false">{{item.fileDetail.filesize/1000000000}} GB</td>
                      <td  v-if="item.favourite!=false">
                        <fwb-dropdown text="Bottom">
                          <template #trigger>
      <span class="dropdown-toggle" id="dropdownMenuButton6" data-toggle="dropdown" aria-expanded="false">
        <i class="ri-more-fill"></i>
      </span>
                          </template>
                          <fwb-list-group class="fwb-list-group">
                            <fwb-list-group-item  class="fwb-list-group-item">
                              Delete
                            </fwb-list-group-item>
                            <fwb-list-group-item  class="fwb-list-group-item" @click="getFiles(item.filename)">
                              Download
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