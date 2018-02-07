INSERT INTO customer (
    tw_user_id,
    screen_name,
    access_token,
    access_token_secret,
    regist_time,
    update_time
)
VALUES (
    '796313650621317120',
    'meiteampower',
    HEX(AES_ENCRYPT('796313650621317120-QLM5A6TVUbAS0OB1lO5GxTA5QblRfE4', 'a237f1fe2feea4a8726d6cb7c82fbd73ba11afd95542b68102022588b870135d62001da3eae82290aa80552e4cf3093aa5243831755af10bc89a0765382d37a8')),
    HEX(AES_ENCRYPT('fSX6OQTvCs0bRC3saZdrzxjWyhZCKRafxC61P8OXctZLV', 'a237f1fe2feea4a8726d6cb7c82fbd73ba11afd95542b68102022588b870135d62001da3eae82290aa80552e4cf3093aa5243831755af10bc89a0765382d37a8')),
    NOW(), NOW()
);


INSERT INTO customer (
    tw_user_id,
    screen_name,
    access_token,
    access_token_secret,
    regist_time,
    update_time
)
VALUES (
    '14896486',
    'tatakauashi',
    HEX(AES_ENCRYPT('14896486-yxQpfo7s4esp3qJHct5bEAFh3CGLXtyp1MbuMZhoS', '2893dcd5f2c23eedc68b2c383c3130c31a20db854b0194313e2dff7dbac7808dc1111bd232e3fe94a529126232f7c5f294589fdb3ef6c86cd2a5282c3aef3712')),
    HEX(AES_ENCRYPT('RwWkG1rG4YPqR0HWUeXPs61cElfhZO1Uj33VvMCplKRYw', '2893dcd5f2c23eedc68b2c383c3130c31a20db854b0194313e2dff7dbac7808dc1111bd232e3fe94a529126232f7c5f294589fdb3ef6c86cd2a5282c3aef3712')),
    NOW(), NOW()
);

