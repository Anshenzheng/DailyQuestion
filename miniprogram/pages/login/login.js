const app = getApp();

Page({
  data: {
    canIUseGetUserProfile: false
  },

  onLoad() {
    if (wx.getUserProfile) {
      this.setData({
        canIUseGetUserProfile: true
      });
    }
    
    const token = wx.getStorageSync('token');
    if (token) {
      app.globalData.token = token;
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },

  getUserProfile() {
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (res) => {
        this.doLogin(res.userInfo);
      },
      fail: (err) => {
        console.log('获取用户信息失败', err);
        this.doLogin(null);
      }
    });
  },

  handleLogin(e) {
    const userInfo = e.detail.userInfo;
    this.doLogin(userInfo);
  },

  async doLogin(userInfo) {
    wx.showLoading({
      title: '登录中...',
      mask: true
    });

    try {
      const loginRes = await this.wxLogin();
      const code = loginRes.code;

      const requestData = { code: code };
      if (userInfo) {
        requestData.nickname = userInfo.nickName;
        requestData.avatarUrl = userInfo.avatarUrl;
        requestData.gender = userInfo.gender;
      }

      const response = await app.request({
        url: '/auth/login',
        method: 'POST',
        data: requestData
      });

      app.globalData.token = response.token;
      app.globalData.userInfo = {
        userId: response.userId,
        nickname: response.nickname,
        avatarUrl: response.avatarUrl
      };

      wx.setStorageSync('token', response.token);
      wx.setStorageSync('userInfo', app.globalData.userInfo);

      wx.hideLoading();
      wx.switchTab({
        url: '/pages/index/index'
      });
    } catch (err) {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '登录失败',
        icon: 'none'
      });
    }
  },

  wxLogin() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          if (res.code) {
            resolve(res);
          } else {
            reject(new Error('获取code失败'));
          }
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
  }
});
