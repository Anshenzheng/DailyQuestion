const app = getApp();

const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

Page({
  data: {
    historyList: []
  },

  onLoad() {
    this.loadHistory();
  },

  onShow() {
    this.loadHistory();
  },

  async loadHistory() {
    wx.showLoading({ title: '加载中...' });

    try {
      const response = await app.request({
        url: '/answers/history',
        method: 'GET'
      });

      const historyList = this.formatHistoryList(response || []);

      this.setData({ historyList });
      wx.hideLoading();
    } catch (err) {
      wx.hideLoading();
      wx.showToast({
        title: err.message || '加载失败',
        icon: 'none'
      });
    }
  },

  formatHistoryList(answers) {
    return answers.map(answer => {
      const answerDate = new Date(answer.answerDate);
      const day = answerDate.getDate();
      const month = answerDate.getMonth() + 1;
      const weekday = weekdays[answerDate.getDay()];

      return {
        ...answer,
        day: day,
        month: month,
        weekday: weekday,
        hasAnswered: true,
        questionContent: answer.questionContent || ''
      };
    });
  },

  viewDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/detail/detail?id=${id}`
    });
  },

  goToToday() {
    wx.switchTab({
      url: '/pages/index/index'
    });
  },

  onPullDownRefresh() {
    this.loadHistory().then(() => {
      wx.stopPullDownRefresh();
    });
  }
});
