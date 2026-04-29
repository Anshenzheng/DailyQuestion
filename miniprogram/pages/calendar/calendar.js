const app = getApp();

Page({
  data: {
    currentYear: 0,
    currentMonth: 0,
    weekDays: ['日', '一', '二', '三', '四', '五', '六'],
    calendarDays: [],
    today: null,
    selectedDayQuestion: null,
    selectedDayAnswer: null,
    selectedDayDate: '',
    answeredDaysMap: {}
  },

  onLoad() {
    const now = new Date();
    this.setData({
      currentYear: now.getFullYear(),
      currentMonth: now.getMonth() + 1,
      today: {
        year: now.getFullYear(),
        month: now.getMonth() + 1,
        day: now.getDate()
      }
    });
    this.generateCalendar();
  },

  onShow() {
    this.loadMonthAnswers();
  },

  generateCalendar() {
    const { currentYear, currentMonth, today } = this.data;
    
    const firstDay = new Date(currentYear, currentMonth - 1, 1);
    const lastDay = new Date(currentYear, currentMonth, 0);
    
    const firstDayWeek = firstDay.getDay();
    const totalDays = lastDay.getDate();
    
    const prevMonthLastDay = new Date(currentYear, currentMonth - 1, 0).getDate();
    
    const calendarDays = [];
    
    for (let i = firstDayWeek - 1; i >= 0; i--) {
      const day = prevMonthLastDay - i;
      const prevMonth = currentMonth === 1 ? 12 : currentMonth - 1;
      const prevYear = currentMonth === 1 ? currentYear - 1 : currentYear;
      calendarDays.push({
        day: day,
        date: this.formatDate(prevYear, prevMonth, day),
        isCurrentMonth: false,
        isToday: false,
        hasAnswered: false,
        answerId: null
      });
    }
    
    for (let day = 1; day <= totalDays; day++) {
      const isToday = today.year === currentYear && today.month === currentMonth && today.day === day;
      const dateStr = this.formatDate(currentYear, currentMonth, day);
      
      calendarDays.push({
        day: day,
        date: dateStr,
        isCurrentMonth: true,
        isToday: isToday,
        hasAnswered: false,
        answerId: null
      });
    }
    
    const remainingDays = 42 - calendarDays.length;
    for (let day = 1; day <= remainingDays; day++) {
      const nextMonth = currentMonth === 12 ? 1 : currentMonth + 1;
      const nextYear = currentMonth === 12 ? currentYear + 1 : currentYear;
      calendarDays.push({
        day: day,
        date: this.formatDate(nextYear, nextMonth, day),
        isCurrentMonth: false,
        isToday: false,
        hasAnswered: false,
        answerId: null
      });
    }
    
    this.setData({ calendarDays });
    this.updateCalendarWithAnswers();
  },

  formatDate(year, month, day) {
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
  },

  async loadMonthAnswers() {
    const { currentYear, currentMonth } = this.data;
    
    try {
      const response = await app.request({
        url: `/answers/calendar/${currentYear}/${currentMonth}`,
        method: 'GET'
      });

      const answeredDaysMap = {};
      if (response && response.length > 0) {
        response.forEach(day => {
          answeredDaysMap[day.date] = day.answerId;
        });
      }

      this.setData({ answeredDaysMap });
      this.updateCalendarWithAnswers();
    } catch (err) {
      console.log('加载月度回答失败', err);
    }
  },

  updateCalendarWithAnswers() {
    const { calendarDays, answeredDaysMap } = this.data;
    
    const updatedDays = calendarDays.map(day => {
      if (answeredDaysMap[day.date]) {
        return {
          ...day,
          hasAnswered: true,
          answerId: answeredDaysMap[day.date]
        };
      }
      return day;
    });

    this.setData({ calendarDays: updatedDays });
  },

  prevMonth() {
    let { currentYear, currentMonth } = this.data;
    currentMonth--;
    if (currentMonth < 1) {
      currentMonth = 12;
      currentYear--;
    }
    this.setData({ 
      currentYear, 
      currentMonth,
      selectedDayQuestion: null,
      selectedDayAnswer: null,
      selectedDayDate: ''
    });
    this.generateCalendar();
    this.loadMonthAnswers();
  },

  nextMonth() {
    let { currentYear, currentMonth } = this.data;
    currentMonth++;
    if (currentMonth > 12) {
      currentMonth = 1;
      currentYear++;
    }
    this.setData({ 
      currentYear, 
      currentMonth,
      selectedDayQuestion: null,
      selectedDayAnswer: null,
      selectedDayDate: ''
    });
    this.generateCalendar();
    this.loadMonthAnswers();
  },

  async selectDay(e) {
    const { date, hasAnswered, answerId } = e.currentTarget.dataset;
    const { today, currentYear, currentMonth } = this.data;
    
    const selectedDate = new Date(date);
    const todayDate = new Date(today.year, today.month - 1, today.day);
    
    if (selectedDate > todayDate) {
      wx.showToast({
        title: '不能查看未来的问题',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '加载中...' });

    try {
      const question = await app.request({
        url: `/daily-question/date/${date}`,
        method: 'GET'
      });

      let answer = null;
      if (hasAnswered && answerId) {
        answer = await app.request({
          url: `/answers/${answerId}`,
          method: 'GET'
        });
      }

      const dateObj = new Date(date);
      const month = dateObj.getMonth() + 1;
      const day = dateObj.getDate();
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      const weekday = weekdays[dateObj.getDay()];

      this.setData({
        selectedDayQuestion: question,
        selectedDayAnswer: answer,
        selectedDayDate: `${month}月${day}日 ${weekday}`
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

  goToDayAnswer() {
    wx.showToast({
      title: '请在首页回答今日问题',
      icon: 'none'
    });
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      urls: [url]
    });
  }
});
