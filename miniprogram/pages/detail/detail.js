const app = getApp();

const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

Page({
  data: {
    answerId: null,
    answer: null,
    day: '',
    monthYear: '',
    weekday: '',
    answerTime: ''
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ answerId: options.id });
      this.loadAnswerDetail(options.id);
    }
  },

  async loadAnswerDetail(id) {
    wx.showLoading({ title: '加载中...' });

    try {
      const answer = await app.request({
        url: `/answers/${id}`,
        method: 'GET'
      });

      const answerDate = new Date(answer.answerDate);
      const day = answerDate.getDate();
      const month = answerDate.getMonth() + 1;
      const year = answerDate.getFullYear();
      const weekday = weekdays[answerDate.getDay()];

      let answerTime = '';
      if (answer.createTime) {
        const createDate = new Date(answer.createTime);
        const hours = String(createDate.getHours()).padStart(2, '0');
        const minutes = String(createDate.getMinutes()).padStart(2, '0');
        answerTime = `${hours}:${minutes}`;
      }

      this.setData({
        answer: answer,
        day: day,
        monthYear: `${month}月 ${year}年`,
        weekday: weekday,
        answerTime: answerTime
      });

      wx.setNavigationBarTitle({
        title: `${month}月${day}日`
      });

      wx.hideLoading();
    } catch (err) {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      });
    }
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      urls: [url]
    });
  },

  onShareAppMessage() {
    const { answer, day, monthYear } = this.data;
    return {
      title: `这是我在${monthYear.replace('年', '')}${day}日的回答：${answer.questionContent}`,
      path: `/pages/login/login`
    };
  }
});
