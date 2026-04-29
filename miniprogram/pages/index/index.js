const app = getApp();

const categoryMap = {
  'daily': '日常',
  'work': '工作',
  'life': '生活',
  'emotion': '情感'
};

Page({
  data: {
    userInfo: null,
    dailyQuestion: null,
    hasAnswered: false,
    myAnswer: null,
    todayWeekday: '',
    todayDate: '',
    categoryText: '日常',
    answerContent: '',
    selectedImage: '',
    submitting: false,
    isEditing: false
  },

  onLoad() {
    this.initDate();
    this.loadUserInfo();
  },

  onShow() {
    this.loadTodayQuestion();
  },

  initDate() {
    const now = new Date();
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    const weekday = weekdays[now.getDay()];
    const month = now.getMonth() + 1;
    const day = now.getDate();
    this.setData({
      todayWeekday: weekday,
      todayDate: `${month}月${day}日`
    });
  },

  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      this.setData({ userInfo });
    }
  },

  async loadTodayQuestion() {
    wx.showLoading({ title: '加载中...' });
    
    try {
      const question = await app.request({
        url: '/daily-question/today',
        method: 'GET'
      });

      this.setData({
        dailyQuestion: question,
        hasAnswered: question.hasAnswered,
        categoryText: categoryMap[question.category] || '日常'
      });

      if (question.hasAnswered) {
        await this.loadTodayAnswer();
      }

      wx.hideLoading();
    } catch (err) {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      });
    }
  },

  async loadTodayAnswer() {
    const today = new Date();
    const dateStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
    
    try {
      const answer = await app.request({
        url: `/answers/date/${dateStr}`,
        method: 'GET'
      });

      if (answer) {
        this.setData({
          myAnswer: answer,
          answerContent: answer.content || '',
          selectedImage: answer.imageUrl || ''
        });
      }
    } catch (err) {
      console.log('获取回答失败', err);
    }
  },

  onContentInput(e) {
    this.setData({ answerContent: e.detail.value });
  },

  chooseImage() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        this.setData({ selectedImage: tempFilePath });
      }
    });
  },

  removeImage() {
    this.setData({ selectedImage: '' });
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      urls: [url]
    });
  },

  editAnswer() {
    this.setData({ 
      isEditing: true,
      hasAnswered: false 
    });
  },

  async submitAnswer() {
    const { answerContent, selectedImage, dailyQuestion, myAnswer } = this.data;
    
    if (!answerContent && !selectedImage) {
      wx.showToast({
        title: '请填写回答或添加图片',
        icon: 'none'
      });
      return;
    }

    this.setData({ submitting: true });

    try {
      let imageUrl = myAnswer?.imageUrl || '';
      
      if (selectedImage && selectedImage !== imageUrl) {
        imageUrl = await app.uploadImage(selectedImage);
      } else if (!selectedImage && imageUrl) {
        imageUrl = '';
      }

      const response = await app.request({
        url: '/answers',
        method: 'POST',
        data: {
          dailyQuestionId: dailyQuestion.id,
          content: answerContent,
          imageUrl: imageUrl
        }
      });

      this.setData({
        submitting: false,
        hasAnswered: true,
        isEditing: false,
        myAnswer: response
      });

      wx.showToast({
        title: '提交成功',
        icon: 'success'
      });
    } catch (err) {
      this.setData({ submitting: false });
      wx.showToast({
        title: err.message || '提交失败',
        icon: 'none'
      });
    }
  },

  onPullDownRefresh() {
    this.loadTodayQuestion().then(() => {
      wx.stopPullDownRefresh();
    });
  }
});
