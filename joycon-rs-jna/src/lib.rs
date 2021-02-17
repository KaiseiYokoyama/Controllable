#![feature(duration_constants)]
#![feature(box_syntax)]

use std::sync::{Mutex, Arc};
use once_cell::sync::Lazy;
use once_cell::sync::OnceCell;
use joycon_rs::prelude::{*, device::calibration::{imu::*, stick::*}};
use joycon_rs::prelude::input_report_mode::*;
use joycon_rs::prelude::input_report_mode::standard_full_mode::IMUData;
use joycon_rs::prelude::device::color;

static HID_API: Lazy<Arc<Mutex<HidApi>>> = Lazy::new(|| {
    Arc::new(Mutex::new(HidApi::new().unwrap()))
});

/// 使用するプロコン（1つ）
static DEVICE: OnceCell<Arc<Mutex<JoyConDevice>>> = OnceCell::new();

/// 使用するプロコンのドライバ
static PRO_CONTROLLER: OnceCell<StandardFullMode<SimpleJoyConDriver>> = OnceCell::new();

#[derive(Debug, Clone, Hash, Eq, PartialEq)]
pub struct JoyConDetails {
    pub stick_parameters: StickParameters,
    pub stick_factory_calibration: JoyConSticksCalibration,
    pub stick_user_calibration: JoyConSticksCalibration,
    pub imu_offsets: IMUOffsets,
    pub imu_factory_calibration: IMUCalibration,
    pub imu_user_calibration: IMUCalibration,
    pub color: color::Color,
}

static PRO_CONTROLLER_DETAILS: OnceCell<JoyConDetails> = OnceCell::new();

/// リフレッシュレート
// static REFRESH_RATE: Lazy<u32> = Lazy::new(|| { 30 });
static REFRESH_RATE: u32 = 30;

/// 最新のコントローラのレポート
static LATEST_REPORT: Lazy<Arc<Mutex<Option<StandardInputReport<IMUData>>>>> = Lazy::new(|| {
    Arc::new(Mutex::new(None))
});

/// プロコンのレポートを受け取り，`LATEST_REPORT`に逐次セットするスレッド
static CONTROLLER_HANDLER: Lazy<std::thread::JoinHandle<()>> = Lazy::new(|| {
    std::thread::spawn(|| {
        loop {
            if let Some(controller) = PRO_CONTROLLER.get() {
                if let Ok(report) = controller.read_input_report() {
                    match LATEST_REPORT.try_lock() {
                        Ok(mut lock) => {
                            *lock = Some(report.clone());
                        }
                        Err(_) => {}
                    }
                }
            } else {
                // 何もしない
            }
            // std::thread::sleep(std::time::Duration::SECOND / REFRESH_RATE)
        }
    })
});

static INITED: OnceCell<()> = OnceCell::new();

/// スティックの数値は両軸ともに[-1,1]，キャリブレーション済み
#[repr(C)]
#[derive(Debug, Default)]
pub struct LatestReport {
    pub input_report_id: i16,
    pub timer: i16,
    pub is_powered: bool,
    #[deprecated]
    pub dummy0: [bool;3],
    pub a_button: bool,
    #[deprecated]
    pub dummy1: [bool;3],
    pub x_button: bool,
    #[deprecated]
    pub dummy2: [bool;3],
    pub y_button: bool,
    #[deprecated]
    pub dummy3: [bool;3],
    pub b_button: bool,
    #[deprecated]
    pub dummy4: [bool;3],
    pub plus_button: bool,
    #[deprecated]
    pub dummy5: [bool;3],
    pub r_stick_press: bool,
    #[deprecated]
    pub dummy6: [bool;3],
    pub home_button: bool,
    #[deprecated]
    pub dummy7: [bool;3],
    pub r_button: bool,
    #[deprecated]
    pub dummy8: [bool;3],
    pub zr_button: bool,
    #[deprecated]
    pub dummy9: [bool;3],
    pub dpad_right: bool,
    #[deprecated]
    pub dummy10: [bool;3],
    pub dpad_up: bool,
    #[deprecated]
    pub dummy11: [bool;3],
    pub dpad_left: bool,
    #[deprecated]
    pub dummy12: [bool;3],
    pub dpad_down: bool,
    #[deprecated]
    pub dummy13: [bool;3],
    pub minus_button: bool,
    #[deprecated]
    pub dummy14: [bool;3],
    pub l_stick_press: bool,
    #[deprecated]
    pub dummy15: [bool;3],
    pub capture_button: bool,
    #[deprecated]
    pub dummy16: [bool;3],
    pub l_button: bool,
    #[deprecated]
    pub dummy17: [bool;3],
    pub zl_button: bool,
    #[deprecated]
    pub dummy18: [bool;3],
    pub sl_button: bool,
    #[deprecated]
    pub dummy19: [bool;3],
    pub sr_button: bool,
    #[deprecated]
    pub dummy20: [bool;3],
    pub left_stick_h: f64,
    pub left_stick_v: f64,
    pub right_stick_h: f64,
    pub right_stick_v: f64,
    pub vibrator_input_report: i16,
}

fn initialize() {
    match INITED.set(()) {
        Ok(()) => {
            // initialize
            let _ = &CONTROLLER_HANDLER.thread();
        }
        Err(()) => {}
    }
}

#[no_mangle]
pub extern fn getLatestReport() -> Box<LatestReport> {
    let report = match LATEST_REPORT.try_lock() {
        Ok(lock) => if let Some(report) = &*lock {
            report.common.clone()
        } else {
            return box LatestReport::default();
        }
        Err(_) => {
            return box LatestReport::default();
        }
    };

    let CommonReport {
        input_report_id,
        timer,
        battery: _,
        connection_info,
        pushed_buttons,
        left_analog_stick_data,
        right_analog_stick_data,
        vibrator_input_report,
    } = report;

    let (left_stick_h, left_stick_v) = {
        let (horizontal, vertical) = (
            left_analog_stick_data.horizontal as i32,
            left_analog_stick_data.vertical as i32
        );

        let ((lx_max, lx_center, lx_min), (ly_max, ly_center, ly_min)) = if let Some(details) = PRO_CONTROLLER_DETAILS.get() {
            calibration::l_stick(details)
        } else {
            ((3500, 2000, 500), (3500, 2000, 500))
        };

        (
            (horizontal - lx_center) as f64 / (lx_max - lx_min) as f64 * 2.0,
            (vertical - ly_center) as f64 / (ly_max - ly_min) as f64 * 2.0,
        )
    };

    let (right_stick_h, right_stick_v) = {
        let (horizontal, vertical) = (
            right_analog_stick_data.horizontal as i32,
            right_analog_stick_data.vertical as i32
        );

        let ((rx_max, rx_center, rx_min), (ry_max, ry_center, ry_min)) = if let Some(details) = PRO_CONTROLLER_DETAILS.get() {
            calibration::r_stick(details)
        } else {
            ((3500, 2000, 500), (3500, 2000, 500))
        };


        (
            (horizontal - rx_center) as f64 / (rx_max - rx_min) as f64 * 2.0,
            (vertical - ry_center) as f64 / (ry_max - ry_min) as f64 * 2.0,
        )
    };

    let report =  LatestReport {
        input_report_id: input_report_id as i16,
        timer: timer as i16,
        // battery,
        is_powered: connection_info.is_powered,
        a_button: pushed_buttons.contains(Buttons::A),
        x_button: pushed_buttons.contains(Buttons::X),
        y_button: pushed_buttons.contains(Buttons::Y),
        b_button: pushed_buttons.contains(Buttons::B),
        plus_button: pushed_buttons.contains(Buttons::Plus),
        r_stick_press: pushed_buttons.contains(Buttons::RStick),
        home_button: pushed_buttons.contains(Buttons::Home),
        r_button: pushed_buttons.contains(Buttons::R),
        zr_button: pushed_buttons.contains(Buttons::ZR),
        dpad_right: pushed_buttons.contains(Buttons::Right),
        dpad_up: pushed_buttons.contains(Buttons::Up),
        dpad_left: pushed_buttons.contains(Buttons::Left),
        dpad_down: pushed_buttons.contains(Buttons::Down),
        minus_button: pushed_buttons.contains(Buttons::Minus),
        l_stick_press: pushed_buttons.contains(Buttons::LStick),
        capture_button: pushed_buttons.contains(Buttons::Capture),
        l_button: pushed_buttons.contains(Buttons::L),
        zl_button: pushed_buttons.contains(Buttons::ZL),
        sl_button: pushed_buttons.contains(Buttons::SL),
        sr_button: pushed_buttons.contains(Buttons::SR),
        left_stick_h,
        left_stick_v,
        right_stick_h,
        right_stick_v,
        vibrator_input_report: vibrator_input_report as i16,
        ..Default::default()
    };

    box report
}

#[no_mangle]
pub extern fn dropReport(_: Box<LatestReport>) {
    // https://github.com/drrb/java-rust-example/blob/master/src/main/rust/com/github/drrb/javarust/lib/greetings.rs
    // Do nothing here. Because we own the Greeting here (we're using a Box) and we're not
    // returning it, Rust will assume we don't want it anymore and clean it up.
}

/// 接続されているデバイスをスキャンする
/// プロコンを検知し，セットした場合は`true`を返し，
/// そうでない場合は`false`を返す
#[no_mangle]
pub extern fn scan() -> bool {
    // early return
    if PRO_CONTROLLER.get().is_some() {
        return false;
    }

    let mut detected = false;

    let hid_api = match HID_API.lock() {
        Ok(hid_api) => hid_api,
        Err(e) => e.into_inner(),
    };

    hid_api.device_list()
        .filter(|&device_info|
            match JoyConDevice::check_type_of_device(device_info) {
                Ok(JoyConDeviceType::ProCon) => true,
                _ => false,
            }
        )
        .flat_map(|device_info| JoyConDevice::new(device_info, &hid_api))
        .flat_map(|device| {
            let details = JoyConDetails {
                stick_parameters: device.stick_parameters().clone(),
                stick_user_calibration: device.stick_user_calibration().clone(),
                stick_factory_calibration: device.stick_factory_calibration().clone(),
                imu_offsets: device.imu_offsets().clone(),
                imu_user_calibration: device.imu_user_calibration().clone(),
                imu_factory_calibration: device.imu_factory_calibration().clone(),
                color: device.color().clone(),
            };

            Some((Arc::new(Mutex::new(device)), details))
        })
        .try_for_each::<_,Result<(),()>>(|(device, details)| {
            let driver = SimpleJoyConDriver::new(&device).map_err(|_| ())?;
            let sfm = StandardFullMode::new(driver).map_err(|_| ())?;

            DEVICE.set(device).map_err(|_| ())?;
            PRO_CONTROLLER.set(sfm).map_err(|_| ())?;
            PRO_CONTROLLER_DETAILS.set(details).map_err(|_| ())?;

            detected = true;

            Ok(())
        });

    if detected {
        initialize();
    }

    return detected;
}

mod calibration {
    use super::*;
    use joycon_rs::prelude::device::calibration::stick::StickCalibration;

    pub fn r_stick(details: &JoyConDetails) -> ((i32, i32, i32), (i32, i32, i32)) {
        match details.stick_user_calibration.right() {
            StickCalibration::Available { x, y } => (
                (x.max() as i32, x.center() as i32, x.min() as i32),
                (y.max() as i32, y.center() as i32, y.min() as i32),
            ),
            StickCalibration::Unavailable => match details.stick_factory_calibration.right() {
                StickCalibration::Available { x, y } => (
                    (x.max() as i32, x.center() as i32, x.min() as i32),
                    (y.max() as i32, y.center() as i32, y.min() as i32),
                ),
                StickCalibration::Unavailable => ((3500, 2000, 500), (3500, 2000, 500)),
            },
        }
    }

    pub fn l_stick(details: &JoyConDetails) -> ((i32, i32, i32), (i32, i32, i32)) {
        match details.stick_user_calibration.left() {
            StickCalibration::Available { x, y } => (
                (x.max() as i32, x.center() as i32, x.min() as i32),
                (y.max() as i32, y.center() as i32, y.min() as i32),
            ),
            StickCalibration::Unavailable => match details.stick_factory_calibration.left() {
                StickCalibration::Available { x, y } => (
                    (x.max() as i32, x.center() as i32, x.min() as i32),
                    (y.max() as i32, y.center() as i32, y.min() as i32),
                ),
                StickCalibration::Unavailable => ((3500, 2000, 500), (3500, 2000, 500)),
            },
        }
    }
}

/// test test ... Base: 0x7ffee52c3478
/// A button: 0x7ffee52c347d
/// Left Stick - Vertical: 0x7ffee52c349a
#[test]
fn test() {
    {
        let report = LatestReport::default();
        println!("Base: {:p}", &report);
        println!("Input Report ID: {:p}", &report.input_report_id);
        println!("Timer: {:p}", &report.timer);
        println!("Is powered: {:p}", &report.is_powered);
        // println!("Dummy 0: {:p}", &report.dummy0);
        println!("A button: {:p}", &report.a_button);
        println!("X button: {:p}", &report.x_button);
        println!("Y button: {:p}", &report.y_button);
        println!("B button: {:p}", &report.b_button);
        println!("Plus button: {:p}", &report.plus_button);
        println!("R Stick: {:p}", &report.r_stick_press);
        println!("Home button: {:p}", &report.home_button);
        println!("R button: {:p}", &report.r_button);
        println!("ZR button: {:p}", &report.zr_button);
        println!("Right button: {:p}", &report.dpad_right);
        println!("Up button: {:p}", &report.dpad_up);
        println!("Left button: {:p}", &report.dpad_left);
        println!("Down button: {:p}", &report.dpad_down);
        println!("Minus button: {:p}", &report.minus_button);
        println!("L Stick: {:p}", &report.l_stick_press);
        println!("Capture button: {:p}", &report.capture_button);
        println!("L button: {:p}", &report.l_button);
        println!("ZL button: {:p}", &report.zl_button);
        println!("SL button: {:p}", &report.sl_button);
        println!("SR button: {:p}", &report.sr_button);
        println!("Left Stick - Horizontal: {:p}", &report.left_stick_h);
        println!("Left Stick - Vertical: {:p}", &report.left_stick_v);
        println!("Right Stick - Horizontal: {:p}", &report.right_stick_h);
        println!("Right Stick - Vertical: {:p}", &report.right_stick_v);
        println!("Vibrator input report: {:p}", &report.vibrator_input_report);
    }

    let mut detected = false;
    loop {
        if scan() {
            detected = true;
        }

        if detected {
            dbg!(getLatestReport());
        }

        std::thread::sleep(std::time::Duration::SECOND);
    }
}
