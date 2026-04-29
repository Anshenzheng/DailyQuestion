App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://localhost:8080/api'
  },

  onLaunch() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    if (token) {
      this.globalData.token = token;
    }
    if (userInfo) {
      this.globalData.userInfo = userInfo;
    }
  },

  request(options) {
    const token = this.globalData.token;
    return new Promise((resolve, reject) => {
      wx.request({
        url: this.globalData.baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data || {},
        header: {
          'Content-Type': 'application/json',
          'Authorization': token ? 'Bearer ' + token : ''
        },
        success: (res) => {
          if (res.statusCode === 401) {
            this.globalData.token = null;
            wx.removeStorageSync('token');
            wx.redirectTo({
              url: '/pages/login/login'
            });
            reject(new Error('登录已过期'));
            return;
          }
          if (res.data.code === 200) {
            resolve(res.data.data);
          } else {
            reject(new Error(res.data.message || '请求失败'));
          }
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
  },

  uploadImage(filePath) {
    const token = this.globalData.token;
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: this.globalData.baseUrl + '/files/upload',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': token ? 'Bearer ' + token : ''
        },
        success: (res) => {
          const data = JSON.parse(res.data);
          if (data.code === 200) {
            resolve(data.data.url);
          } else {
            reject(new Error(data.message || '上传失败'));
          }
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
  }
});
