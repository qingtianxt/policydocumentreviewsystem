var s = echarts.init(document.getElementById("echarts-map-chart")),
	c = {
		title: {
			text: "各地关于《继续执行宣传文化增值税和营业税优惠政策的通知》的执行情况",
			x: "center"
		},
		tooltip: {
			trigger: "item"
		},
		legend: {
			orient: "vertical",
			x: "left",
			data: ["创新条款", "审查通过文档", "政策投入"]
		},
		dataRange: {
			min: 0,
			max: 2500,
			x: "left",
			y: "bottom",
			text: ["高", "低"],
			calculable: !0
		},
		toolbox: {
			show: !0,
			orient: "vertical",
			x: "right",
			y: "center",
			feature: {
				mark: {
					show: !0
				},
				dataView: {
					show: !0,
					readOnly: !1
				},
				restore: {
					show: !0
				},
				saveAsImage: {
					show: !0
				}
			}
		},
		roamController: {
			show: !0,
			x: "right",
			mapTypeControl: {
				china: !0
			}
		},
		series: [{
			name: "创新条款",
			type: "map",
			mapType: "china",
			roam: !1,
			itemStyle: {
				normal: {
					label: {
						show: !0
					}
				},
				emphasis: {
					label: {
						show: !0
					}
				}
			},
			data: [{
				name: "北京",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "天津",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "上海",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "重庆",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "河北",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "河南",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "云南",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "辽宁",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "黑龙江",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "湖南",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "安徽",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "山东",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "新疆",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "江苏",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "浙江",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "江西",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "湖北",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "广西",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "甘肃",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "山西",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "内蒙古",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "陕西",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "吉林",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "福建",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "贵州",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "广东",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "青海",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "西藏",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "四川",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "宁夏",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "海南",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "台湾",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "香港",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "澳门",
				value: Math.round(1e3 * Math.random())
			}]
		}, {
			name: "审查通过文档",
			type: "map",
			mapType: "china",
			itemStyle: {
				normal: {
					label: {
						show: !0
					}
				},
				emphasis: {
					label: {
						show: !0
					}
				}
			},
			data: [{
				name: "北京",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "天津",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "上海",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "重庆",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "河北",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "安徽",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "新疆",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "浙江",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "江西",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "山西",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "内蒙古",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "吉林",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "福建",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "广东",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "西藏",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "四川",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "宁夏",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "香港",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "澳门",
				value: Math.round(1e3 * Math.random())
			}]
		}, {
			name: "政策投入",
			type: "map",
			mapType: "china",
			itemStyle: {
				normal: {
					label: {
						show: !0
					}
				},
				emphasis: {
					label: {
						show: !0
					}
				}
			},
			data: [{
				name: "北京",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "天津",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "上海",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "广东",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "台湾",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "香港",
				value: Math.round(1e3 * Math.random())
			}, {
				name: "澳门",
				value: Math.round(1e3 * Math.random())
			}]
		}]
	};
s.setOption(c), $(window).resize(s.resize);